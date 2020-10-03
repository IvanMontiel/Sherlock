package mx.brennen.sherlock


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.webkit.WebSettings
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import katex.hourglass.`in`.mathlib.MathView
import kotlinx.android.synthetic.main.fragment_lagrange_interpolation.*
import mx.brennen.sherlock.res.Math
import mx.brennen.sherlock.res.NumericalMethods
import mx.brennen.sherlock.res.PersonalAdapterInterpolation
import mx.brennen.sherlock.res.misc.TypefaceUtil
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.padding
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onTouch
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.uiThread
import org.jetbrains.anko.windowManager

class LagrangeInterpolationFragment : Fragment() {

    private var FUNCTION = ""
    private var width = 0f
    private var DESMOS_STATE = false
    private val X_FIELDS : ArrayList<TextInputEditText> = ArrayList()
    private val Y_FIELDS : ArrayList<TextInputEditText> = ArrayList()
    private val ITEMS : ArrayList<LinearLayout> = ArrayList()
    private lateinit var X_VALUES : DoubleArray
    private lateinit var Y_VALUES : DoubleArray
    private val NM = NumericalMethods()
    private val math = Math()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lagrange_interpolation, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val prefs = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        math_model.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        desmos.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        solutions.settings.cacheMode = WebSettings.LOAD_NO_CACHE

        doAsync {

            val size = Point()
            requireContext().windowManager.defaultDisplay.getSize(size)
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

        cont.layoutManager = LinearLayoutManager(context)

        super.onViewCreated(view, savedInstanceState)

        calculate.onClick {

            try {

                X_VALUES = DoubleArray(X_FIELDS.size)
                Y_VALUES = DoubleArray(Y_FIELDS.size)
                var index = 0
                for (X in X_FIELDS){

                    try {

                        X_VALUES[index] = X.text.toString().toDouble()
                        index++

                    }catch (e : Exception){

                        X_VALUES[index] = 0.0
                        index++

                    }

                }

                index = 0

                for (Y in Y_FIELDS){

                    try {

                        Y_VALUES[index] = Y.text.toString().toDouble()
                        index++

                    }catch (e : Exception){

                        Y_VALUES[index] = 0.0
                        index++

                    }

                }

                doAsync {

                    val interpreted = math.mathml(NM.lagrangeInterpolation(X_VALUES,Y_VALUES))
                    FUNCTION = interpreted[0]

                    uiThread {

                        math_model.setDisplayText("$${interpreted[1]}$")
                        if(X_FIELDS.size<5){

                            solutions.setDisplayText("$${math.solve(FUNCTION,"x")}$")
                            sol.visibility = View.VISIBLE

                        }else{

                            sol.visibility = View.GONE

                        }
                        rellay.visibility = View.VISIBLE
                        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                        if (DESMOS_STATE){

                            desmos.evaluateJavascript("javascript:setMathModel(\'${FUNCTION}\')",null)
                            if(!math.isFunction(FUNCTION,'x',1.0)){

                                desmos.visibility = View.VISIBLE

                            }

                        }

                    }


                }


            }catch (e : Exception){

                toast("Comprueba tus datos de entrada")

            }

        }

        evaluate.onClick {

            try {

                val valueOf = math.evaluate(FUNCTION,"x",value.text.toString().toDouble())
                val mess = "El resultado de la evaluacion es: $valueOf"
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                valueofecuation.text = mess
                desmos.evaluateJavascript("javascript:setEvaluation(\'(${value.text.toString()},${valueOf})\')",null)

            } catch (e : Exception){

                toast("Comprueba tu entrada")

            }

        }

        aceptar.onClick {

            if(pairs_number.text.toString() != ""){

                try{

                    generateFields(pairs_number.text.toString().toInt())
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    cont.visibility = View.VISIBLE
                    calculate.visibility = View.VISIBLE

                }catch (e : Exception){

                    toast("Comprueba tu entrada")

                }

            }

        }

        infoicon.onClick {

            val builderSymLegal = AlertDialog.Builder(requireContext())
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

            mathView.setDisplayText("$${math.mathml(function.text.toString())[1]}$")

            dialogSymLegal.show()

        }

        menicon.onClick {

            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            (activity as HomeActivity).menu()

        }

    }

    @SuppressLint("InflateParams")
    private fun generateFields(size : Int) {

        X_FIELDS.clear()
        Y_FIELDS.clear()
        ITEMS.clear()

        for ( i in 0 until size){

            val field = LayoutInflater.from(context).inflate(R.layout.item_interpolations,null) as LinearLayout
            field.padding = 5
            val xNumber = i+1

            field.findViewById<TextView>(R.id.X).text = HtmlCompat.fromHtml("X<sub><small>$xNumber</sub></small>", HtmlCompat.FROM_HTML_MODE_LEGACY)
            field.findViewById<TextView>(R.id.Y).text = HtmlCompat.fromHtml("F(X<sub><small>$xNumber</sub></small>)", HtmlCompat.FROM_HTML_MODE_LEGACY)

            Y_FIELDS.add(field.findViewById(R.id.y))
            X_FIELDS.add(field.findViewById(R.id.x))

            ITEMS.add(field)

        }

        cont.adapter = PersonalAdapterInterpolation(ITEMS)

    }

}
