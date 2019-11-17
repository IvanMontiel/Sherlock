package mx.brennen.sherlock

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.webkit.WebSettings
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import katex.hourglass.`in`.mathlib.MathView
import kotlinx.android.synthetic.main.fragment_linear_interpolation.*
import mx.brennen.sherlock.res.CoreServices
import mx.brennen.sherlock.res.misc.TypefaceUtil
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onTouch
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.uiThread
import org.jetbrains.anko.windowManager

class LinearInterpolationFragment : Fragment() {

    private var width = 0f
    private var DESMOS_STATE = false
    private var FUNCTION = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_linear_interpolation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val prefs = context!!.getSharedPreferences("Preferences", Context.MODE_PRIVATE)

        math_model.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        desmos.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        solutions.settings.cacheMode = WebSettings.LOAD_NO_CACHE

        doAsync {

            val size = Point()
            context!!.windowManager.defaultDisplay.getSize(size)
            val scale: Float = resources.displayMetrics.density
            width = ((size.x-(50*scale))/scale)
            DESMOS_STATE = prefs.getBoolean("desmosApi",false)

            desmos.onTouch { _, _ ->

                generalScroll.requestDisallowInterceptTouchEvent(true)

            }

            generalScroll.onTouch { _, _ ->

                generalScroll.requestDisallowInterceptTouchEvent(false)

            }

            if(DESMOS_STATE){

                uiThread {

                    if(desmos!=null){

                        desmos.settings.javaScriptEnabled = true
                        desmos.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                        val html = "<!DOCTYPE html>\n" +
                                "<html lang=\"en\">\n" +
                                "<head>\n" +
                                "    <meta charset=\"UTF-8\">\n" +
                                "    <title>Title</title>\n" +
                                "</head>\n" +
                                "<body>\n" +
                                "\n" +
                                "<script src=\"file:///android_asset/pages/calculator.js\"></script>\n" +
                                "  <div id=\"calculator\" style=\"width: ${width}px; height: 500px;\"></div>\n" +
                                "  <script>\n" +
                                "    var elt = document.getElementById('calculator');\n" +
                                "    var calculator = Desmos.GraphingCalculator(elt,{keyboard:false,expressions:false});\n" +
                                "    function setMathModel(model){\n" +
                                "\n" +
                                "      calculator.setExpression({id: 'function', latex: ('y=' + model)});\n" +
                                "\n" +
                                "    }" +
                                "    function setEvaluation(points){\n" +
                                "\n" +
                                "      calculator.setExpression({id: 'point', latex: (points)});\n" +
                                "\n" +
                                "    }" +
                                "  </script>\n" +
                                "\n" +
                                "</body>\n" +
                                "</html>\n"
                        desmos.loadDataWithBaseURL("file:///android_asset/pages/main.html", html, "text/html", "UTF-8", null)

                    }

                }

            }

        }

        if(prefs.getBoolean("RealTime",false)){

            evaluate.visibility = View.GONE
            calculate.visibility = View.GONE

            x1.addTextChangedListener{

                try {

                    val firstIntervals = DoubleArray(2).apply {

                        if(x1.text.toString() != ""){
                            set(0,x1.text.toString().toDouble())
                        }else{
                            set(0, 0.0)
                        }
                        if(y1.text.toString() != ""){
                            set(1,y1.text.toString().toDouble())
                        }else{
                            set(1,0.0)
                        }

                    }

                    val secondIntervals = DoubleArray(2).apply {

                        if(x2.text.toString() != ""){
                            set(0,x2.text.toString().toDouble())
                        }else{
                            set(0,0.0)
                        }
                        if(y2.text.toString() != ""){
                            set(1,y2.text.toString().toDouble())
                        }else{
                            set(1,0.0)
                        }
                    }

                    if (!firstIntervals[0].isNaN()){

                        val arryFunctions = CoreServices().mathml(CoreServices().linearInterpolation(firstIntervals,secondIntervals))
                        FUNCTION = arryFunctions[0]
                        math_model.setDisplayText("$${arryFunctions[1]}$")
                        rellay.visibility = View.VISIBLE
                        solutions.setDisplayText("$${CoreServices().solve(FUNCTION,"x")}$")
                        sol.visibility = View.VISIBLE
                        if(value.text.toString() != ""){

                            try {

                                val valueOf = CoreServices().evaluate(FUNCTION,"x",value.text.toString().toDouble())
                                val mess = "El resultado de la evaluacion es: $valueOf"
                                desmos.evaluateJavascript("javascript:setEvaluation(\'(${value.text.toString()},${valueOf})\')",null)
                                valueofecuation.text = mess

                            } catch (e : Exception){


                            }

                        }
                        if (DESMOS_STATE){

                            desmos.evaluateJavascript("javascript:setMathModel(\'${FUNCTION}\')",null)
                            if(!CoreServices().isFunction(FUNCTION,'x',1.0)){

                                desmos.visibility = View.VISIBLE

                            }

                        }

                    }

                }catch (e : Exception){

                    toast("Comprueba tus datos de entrada")

                }

            }

            y1.addTextChangedListener{

                try {

                    val firstIntervals = DoubleArray(2).apply {

                        if(x1.text.toString() != ""){
                            set(0,x1.text.toString().toDouble())
                        }else{
                            set(0, 0.0)
                        }
                        if(y1.text.toString() != ""){
                            set(1,y1.text.toString().toDouble())
                        }else{
                            set(1,0.0)
                        }

                    }

                    val secondIntervals = DoubleArray(2).apply {

                        if(x2.text.toString() != ""){
                            set(0,x2.text.toString().toDouble())
                        }else{
                            set(0,0.0)
                        }
                        if(y2.text.toString() != ""){
                            set(1,y2.text.toString().toDouble())
                        }else{
                            set(1,0.0)
                        }
                    }

                    if (!firstIntervals[0].isNaN()){

                        val arryFunctions = CoreServices().mathml(CoreServices().linearInterpolation(firstIntervals,secondIntervals))
                        FUNCTION = arryFunctions[0]
                        math_model.setDisplayText("$${arryFunctions[1]}$")
                        rellay.visibility = View.VISIBLE
                        solutions.setDisplayText("$${CoreServices().solve(FUNCTION,"x")}$")
                        sol.visibility = View.VISIBLE
                        if(value.text.toString() != ""){

                            try {

                                val valueOf = CoreServices().evaluate(FUNCTION,"x",value.text.toString().toDouble())
                                val mess = "El resultado de la evaluacion es: $valueOf"
                                desmos.evaluateJavascript("javascript:setEvaluation(\'(${value.text.toString()},${valueOf})\')",null)
                                valueofecuation.text = mess

                            } catch (e : Exception){


                            }

                        }
                        if (DESMOS_STATE){

                            desmos.evaluateJavascript("javascript:setMathModel(\'${FUNCTION}\')",null)
                            if(!CoreServices().isFunction(FUNCTION,'x',1.0)){

                                desmos.visibility = View.VISIBLE

                            }

                        }

                    }

                }catch (e : Exception){

                    toast("Comprueba tus datos de entrada")

                }

            }

            x2.addTextChangedListener{

                try {

                    val firstIntervals = DoubleArray(2).apply {

                        if(x1.text.toString() != ""){
                            set(0,x1.text.toString().toDouble())
                        }else{
                            set(0, 0.0)
                        }
                        if(y1.text.toString() != ""){
                            set(1,y1.text.toString().toDouble())
                        }else{
                            set(1,0.0)
                        }

                    }

                    val secondIntervals = DoubleArray(2).apply {

                        if(x2.text.toString() != ""){
                            set(0,x2.text.toString().toDouble())
                        }else{
                            set(0,0.0)
                        }
                        if(y2.text.toString() != ""){
                            set(1,y2.text.toString().toDouble())
                        }else{
                            set(1,0.0)
                        }
                    }

                    if (!firstIntervals[0].isNaN()){

                        val arryFunctions = CoreServices().mathml(CoreServices().linearInterpolation(firstIntervals,secondIntervals))
                        FUNCTION = arryFunctions[0]
                        math_model.setDisplayText("$${arryFunctions[1]}$")
                        rellay.visibility = View.VISIBLE
                        solutions.setDisplayText("$${CoreServices().solve(FUNCTION,"x")}$")
                        sol.visibility = View.VISIBLE
                        if(value.text.toString() != ""){

                            try {

                                val valueOf = CoreServices().evaluate(FUNCTION,"x",value.text.toString().toDouble())
                                val mess = "El resultado de la evaluacion es: $valueOf"
                                desmos.evaluateJavascript("javascript:setEvaluation(\'(${value.text.toString()},${valueOf})\')",null)
                                valueofecuation.text = mess

                            } catch (e : Exception){


                            }

                        }
                        if (DESMOS_STATE){

                            desmos.evaluateJavascript("javascript:setMathModel(\'${FUNCTION}\')",null)
                            if(!CoreServices().isFunction(FUNCTION,'x',1.0)){

                                desmos.visibility = View.VISIBLE

                            }

                        }

                    }

                }catch (e : Exception){

                    toast("Comprueba tus datos de entrada")

                }

            }

            y2.addTextChangedListener{

                try {

                    val firstIntervals = DoubleArray(2).apply {

                        if(x1.text.toString() != ""){
                            set(0,x1.text.toString().toDouble())
                        }else{
                            set(0, 0.0)
                        }
                        if(y1.text.toString() != ""){
                            set(1,y1.text.toString().toDouble())
                        }else{
                            set(1,0.0)
                        }

                    }

                    val secondIntervals = DoubleArray(2).apply {

                        if(x2.text.toString() != ""){
                            set(0,x2.text.toString().toDouble())
                        }else{
                            set(0,0.0)
                        }
                        if(y2.text.toString() != ""){
                            set(1,y2.text.toString().toDouble())
                        }else{
                            set(1,0.0)
                        }
                    }

                    if (!firstIntervals[0].isNaN()){

                        val arryFunctions = CoreServices().mathml(CoreServices().linearInterpolation(firstIntervals,secondIntervals))
                        FUNCTION = arryFunctions[0]
                        math_model.setDisplayText("$${arryFunctions[1]}$")
                        rellay.visibility = View.VISIBLE
                        solutions.setDisplayText("$${CoreServices().solve(FUNCTION,"x")}$")
                        sol.visibility = View.VISIBLE
                        if(value.text.toString() != ""){

                            try {

                                val valueOf = CoreServices().evaluate(FUNCTION,"x",value.text.toString().toDouble())
                                val mess = "El resultado de la evaluacion es: $valueOf"
                                desmos.evaluateJavascript("javascript:setEvaluation(\'(${value.text.toString()},${valueOf})\')",null)
                                valueofecuation.text = mess

                            } catch (e : Exception){


                            }

                        }
                        if (DESMOS_STATE){

                            desmos.evaluateJavascript("javascript:setMathModel(\'${FUNCTION}\')",null)
                            if(!CoreServices().isFunction(FUNCTION,'x',1.0)){

                                desmos.visibility = View.VISIBLE

                            }

                        }

                    }

                }catch (e : Exception){

                    toast("Comprueba tus datos de entrada")

                }

            }

            value.addTextChangedListener {

                try {
                    val valueOf = CoreServices().evaluate(FUNCTION,"x",value.text.toString().toDouble())
                    val mess = "El resultado de la evaluacion es: $valueOf"
                    desmos.evaluateJavascript("javascript:setEvaluation(\'(${value.text.toString()},${valueOf})\')",null)
                    valueofecuation.text = mess

                } catch (e : Exception){


                }

            }

        } else {

            calculate.onClick {

                try {

                    val firstIntervals = DoubleArray(2).apply {

                        set(0,x1.text.toString().toDouble())
                        set(1,y1.text.toString().toDouble())

                    }

                    val secondIntervals = DoubleArray(2).apply {

                        set(0,x2.text.toString().toDouble())
                        set(1,y2.text.toString().toDouble())

                    }

                    val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    val funtions = CoreServices().mathml(CoreServices().linearInterpolation(firstIntervals,secondIntervals))
                    FUNCTION = funtions[0]
                    solutions.setDisplayText("$${CoreServices().solve(FUNCTION,"x")}$")
                    math_model.setDisplayText(funtions[1])
                    rellay.visibility = View.VISIBLE
                    if (DESMOS_STATE){

                        desmos.evaluateJavascript("javascript:setMathModel(\'${FUNCTION}\')",null)
                        if(!CoreServices().isFunction(FUNCTION,'x',1.0)){

                            desmos.visibility = View.VISIBLE

                        }

                    }

                }catch (e : Exception){

                    toast("Comprueba tus datos de entrada")

                }

            }

            evaluate.onClick {

                try {

                    val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    val valueOf = CoreServices().evaluate(FUNCTION,"x",value.text.toString().toDouble())
                    val mess = "El resultado de la evaluacion es: $valueOf"
                    desmos.evaluateJavascript("javascript:setEvaluation(\'(${value.text.toString()},${valueOf})\')",null)
                    valueofecuation.text = mess
                    toast(mess)

                } catch (e : Exception){

                    toast("Comprueba tu entrada")

                }

            }

        }

        super.onViewCreated(view, savedInstanceState)

        x_1.text = HtmlCompat.fromHtml("X<sub><small>1</sub></small>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        y_1.text = HtmlCompat.fromHtml("F(X<sub><small>1</sub></small>)", HtmlCompat.FROM_HTML_MODE_LEGACY)
        x_2.text = HtmlCompat.fromHtml("X<sub><small>2</sub></small>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        y_2.text = HtmlCompat.fromHtml("F(X<sub><small>2</sub></small>)", HtmlCompat.FROM_HTML_MODE_LEGACY)

        infoicon.onClick {

            val builderSymLegal = AlertDialog.Builder(context!!)
            val viewSymLegal = layoutInflater.inflate(R.layout.fragment_intro_alpha,null)
            val areeButton = viewSymLegal.findViewById(R.id.aceptar) as TextView
            val mathView = viewSymLegal.findViewById(R.id.interpeter) as MathView
            val function = viewSymLegal.findViewById(R.id.cuar) as TextView

            mathView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            builderSymLegal.setView(viewSymLegal)
            TypefaceUtil().overrideFont(builderSymLegal.context,"SERIF","fonts/arciform.otf")
            val dialogSymLegal = builderSymLegal.create()
            dialogSymLegal.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogSymLegal.window!!.decorView.setBackgroundResource(android.R.color.transparent)

            areeButton.onClick {

                dialogSymLegal.dismiss()
                val editor = prefs.edit()
                editor.putBoolean("firstTime",false)
                editor.apply()

            }

            mathView.setDisplayText("$${CoreServices().mathml(function.text.toString())[1]}$")

            dialogSymLegal.show()

        }

        menicon.onClick {

            val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            (activity as HomeActivity).menu()

        }

    }

}
