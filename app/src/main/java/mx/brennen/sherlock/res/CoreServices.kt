package mx.brennen.sherlock.res

import com.chaquo.python.Python
import mx.brennen.sherlock.res.misc.Iteracion
import mx.brennen.sherlock.res.misc.IteracionPF
import mx.brennen.sherlock.res.misc.IteracionS
import mx.brennen.sherlock.res.misc.IteracionVI
import java.lang.Double.NaN
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

@Suppress("NAME_SHADOWING")

class CoreServices{

    fun intermediateValue(function: String, `var`: String, Intervalos: DoubleArray, Tolerancia: Double, limit: Int) : ArrayList<IteracionVI>{

        val iterations = ArrayList<IteracionVI>()

        var fa1 = Intervalos[0]
        var fb = Intervalos[1]

        var pn1 = (Intervalos[0] + Intervalos[1]) / 2
        if(evaluate(function,`var`, fa1) * evaluate(function,`var`, fb)<0){

            var iteration1 = 1

            while (true) {

                val iter = IteracionVI()

                if (iteration1 == 1) {

                    val fn1 = evaluate(function,`var`,pn1)
                    var fx1 = evaluate(function,`var`,fa1) * fn1

                    iter.apply {

                        iteracion = iteration1
                        an = Intervalos[0]
                        bn = Intervalos[1]
                        pn = pn1
                        fn = (evaluate(function, `var`, pn1))

                    }

                    iterations.add(iter)

                    if (fx1 < 0) {

                        fx1 *= -1.0
                        if (fx1 < Tolerancia) {

                            break

                        }else{

                            fb = pn1


                        }


                    } else {

                        if (fx1 < Tolerancia) {

                            break

                        }else{

                            fa1 = pn1

                        }


                    }

                    iteration1++

                } else {

                    pn1 = (fa1+fb)/2

                    val fn1 = evaluate(function,`var`,pn1)
                    var fx1 = evaluate(function,`var`,fa1) * fn1

                    iter.apply {

                        iteracion = iteration1
                        an = fa1
                        bn = fb
                        pn = (fa1 + fb) / 2
                        fn = evaluate(function, `var`, pn1)

                    }
                    iterations.add(iter)

                    if (fx1 < 0) {

                        fx1 *= -1.0
                        if (fx1 < Tolerancia) {

                            break

                        }else{

                            fb = pn1


                        }


                    } else {

                        if (fx1 < Tolerancia) {

                            break

                        }else{

                            fa1 = pn1

                        }


                    }

                    iteration1++

                }

                if (limit>0){

                    if (iteration1>limit){

                        break

                    }else if (iteration1>250){

                        break

                    }

                } else if (iteration1 > 250){

                    break

                }

            }

            return iterations

        }

        return iterations

    }

    fun tolerance(tolerance: String) : Double{

        var potence: Double = 0.toDouble()
        val notacion = tolerance.split("\\^".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val caracteres = notacion[1].toCharArray()
        if (caracteres[0] == '-') {

            var value = ""
            for (i in 1 until caracteres.size) {

                value += Character.toString(caracteres[i])

            }

            try {

                potence = -java.lang.Double.valueOf(value)

            } catch (e: Exception) {



            }

        } else {

            val value = notacion[1]

            try {

                potence = Integer.parseInt(value).toDouble()

            } catch (e : Exception){}

        }

        when {
            potence < 0 -> {

                var valor = 1.0
                var i = -1
                while (i >= potence) {

                    valor /= 10.0
                    i--

                }
                
                return valor

            }
            potence > 0 -> {

                var valor = 1.0
                var i = 1
                while (i <= potence) {

                    valor *= 10.0
                    i++

                }
                
                return valor

            }
            else -> println("La Tolerancia insertada no existe / no es un valor")
        }
        
        return 0.0

    }

    fun secant(function: String, variable: String, intervals: DoubleArray, tolerance: Double, limit: Int) : ArrayList<IteracionS> {

        var x0: Double = 0.toDouble()
        var x: Double = 0.toDouble()
        var xx: Double
        var tr: Double
        var ex: Double
        var nIteration = 1
        val iterations: ArrayList<IteracionS> = ArrayList()

        val stop = false

        while (!stop) {

            val it = IteracionS()

            if (nIteration == 1) {

                x0 = intervals[0]
                x = intervals[1]
                val fxn1 = evaluate(function, variable, x)
                val fxn = evaluate(function, variable, x0)
                val xx0 = x - x0

                xx = (x - fxn1 * xx0) / (fxn1 - fxn)

                it.apply {

                    iteracion = nIteration
                    setX(x)
                    setX0(x0)
                    setFxn(fxn)

                }

                iterations.add(it)

                ex = fxn

                if (ex < 0) {


                    tr = ex * -1.0

                    if (tr < tolerance) {

                        break

                    } else {

                        x0 = x
                        x = xx

                    }

                } else {

                    if (ex < tolerance) {

                        break

                    } else {

                        x0 = x
                        x = xx

                    }

                }

                nIteration++

            } else {

                val fxn1 = evaluate(function, variable, x)
                val fxn = evaluate(function, variable, x0)
                val xx0 = x - x0

                xx = (x - fxn1 * xx0 / (fxn1 - fxn))

                if (x.isNaN()){
                    break
                }

                it.apply {

                    iteracion = nIteration
                    setX(x)
                    setX0(x0)
                    setFxn(fxn)

                }

                iterations.add(it)

                ex = fxn

                if (ex < 0) {

                    tr = ex * -1.0

                    if (tr < tolerance) {

                        break

                    } else {

                        x0 = x
                        x = xx

                    }

                } else {

                    if (ex < tolerance) {

                        break

                    } else {

                        x0 = x
                        x = xx

                    }

                }

                nIteration++

            }

            if (limit>0){

                if (nIteration>limit){

                    break

                }else if (nIteration>250){

                    break

                }

            }else if (nIteration>250){

                break

            }

        }

        return iterations

    }

    fun fixedPoint(function:String, variable :String, intervals:DoubleArray, tolerance:Double, limit: Int) : ArrayList<IteracionPF> {

        var x0: Double
        var xn: Double
        var cond: Double
        val listIteration = ArrayList<IteracionPF>()
        var niter = 0

        val derive = derive(function, variable)
        val x = "x-(($function)/($derive))"
        val resultFunction = x

        val iteration = IteracionPF()

        x0 = intervals[0]
        xn = evaluate(resultFunction, variable, x0)

        iteration.apply {

            ni = niter
            setValueOf(x0)
            ev = xn

        }

        listIteration.add(iteration)

        cond = x0 - xn

        if (cond < 0)
        {

            cond *= -1

        }

        while (cond > tolerance)
        {

            x0 = xn
            xn = evaluate(resultFunction, variable, x0)

            niter += 1
            val iterationNew = IteracionPF().apply {

                ni = niter
                setValueOf(x0)
                ev = xn

            }
            listIteration.add(iterationNew)

            cond = x0 - xn
            if (cond < 0)
            {

                cond *= -1

            }

            if (limit>0){

                if (niter>limit){

                    break

                }else if (niter>=250){

                    break

                }

            }else if (niter>=250){

                break

            }


        }
        
        return listIteration

    }

    fun falsePosition(function:String, variable :String, intervals:DoubleArray, tolerance:Double, limit:Int) : ArrayList<IteracionVI> {

        var x0 = intervals[0]
        var x = intervals[1]
        var xx:Double
        var tr:Double
        var ex:Double
        var niteration = 1
        val iterations = ArrayList<IteracionVI>()
        var condition  = 0.0

        val stop = false

        while (!stop) {

            val it = IteracionVI()

            if (niteration == 1) {


                val fxn1 = evaluate(function, variable, x)
                val fxn = evaluate(function, variable, x0)
                val xx0 = x - x0

                xx = (x - fxn1 * xx0 / (fxn1 - fxn))
                val fpn = evaluate(function, variable, xx)

                it.apply {

                    iteracion = niteration
                    bn = x
                    an = x0
                    pn = xx
                    fn = fpn

                }

                val fn = evaluate(function, variable, x0) * fpn

                iterations.add(it)

                if (fn > 0){

                    ex = fpn

                    if (ex < 0) {

                        tr = ex * -1.0
                        if (tr < tolerance){

                            break
                        } else {

                            if (fpn > 0){

                                x = xx
                            } else if (fpn < 0) {

                                x0 = xx
                            }
                        }
                    } else {

                        if (ex < tolerance) {

                            break
                        } else {

                            if (fpn > 0) {

                                x = xx

                            } else if (fpn < 0) {

                                x0 = xx
                            }

                        }

                    }

                } else {

                    ex = fpn
                    if (ex < 0){

                        tr = ex * -1.0
                        if (tr < tolerance) {

                            break

                        } else {

                            if (fpn > 0) {

                                x = xx

                            } else if (fpn < 0) {

                                x0 = xx

                            }

                        }

                    } else {

                        if (ex < tolerance) {

                            break

                        } else {

                            if (fpn > 0) {

                                x = xx

                            } else if (fpn < 0) {

                                x0 = xx

                            }

                        }

                    }

                }

                niteration++

            } else {

                val fxn1 = evaluate(function, variable, x)
                val fxn = evaluate(function, variable, x0)
                val xx0 = x - x0

                xx = (x - fxn1 * xx0 / (fxn1 - fxn))
                val fpn = evaluate(function, variable, xx)

                it.apply {

                    iteracion = niteration
                    bn = x
                    an = x0
                    pn = xx
                    fn = fpn

                }

                ex = fpn

                if (ex < 0) {

                    tr = ex * -1.0

                    if (tr < tolerance || condition == ex) {

                        break

                    } else {

                        if (fpn > 0) {

                            x = xx

                        } else if (fpn < 0) {

                            x0 = xx

                        }

                        condition = ex

                    }

                } else {

                    if (ex < tolerance || condition == ex) {

                        break

                    } else {

                        if (fpn > 0) {

                            x = xx

                        } else if (fpn < 0) {

                            x0 = xx

                        }

                        condition = ex

                    }

                }

                iterations.add(it)

                niteration++

                if (limit!=0){

                    if (niteration > limit) {

                        break

                    }else if (niteration > 250){

                        break

                    }

                } else {

                    if (niteration > 250) {

                        break

                    }

                }

            }

        }
        
        return iterations

    }
    
    fun linearInterpolation(Intervals0: DoubleArray, Intervals1: DoubleArray) : String {

        val x0 = Intervals0[0]
        val fx0 = Intervals0[1]

        val x1 = Intervals1[0]
        val fx1 = Intervals1[1]

        val sup = fx1 - fx0
        val inf = x1 - x0

        val div = sup / inf

        return simplify("$fx0+$div*(x-$x0)")

    }

    fun newtonInterpolation(points: DoubleArray, evaluations: DoubleArray) : String {

        val fx0 = evaluations[0]
        var first: DoubleArray?
        var second: DoubleArray?

        val Auxn = ArrayList<DoubleArray>()

        var aux = ""

        var i = 0
        var se = 2

        while (i < evaluations.size) {

            if (i == 0) {

                first = DoubleArray(evaluations.size - 1)

                for (j in 1 until first.size + 1) {

                    first[j - 1] = (evaluations[j] - evaluations[j - 1]) / (points[j] - points[j - 1])

                }

                Auxn.add(first)
                first = null
                i++

            } else {

                first = Auxn[i - 1]
                second = DoubleArray(first.size - 1)

                for (j in 1 until second.size + 1) {

                    second[j - 1] = (first[j] - first[j - 1]) / (points[se + j - 1] - points[j - 1])

                }

                Auxn.add(second)
                first = null
                second = null
                i++
                se++

            }

        }

        var function = ""

        for (j in points.indices) {

            if (j == 0) {

                function = fx0.toString()

            } else {

                aux += "(x-" + points[j - 1].toString() + ")*"
                function += "+" + aux + "(" + Auxn[j - 1][0].toString() + ")"

            }

        }

        return simplify(function)

    }

    fun lagrangeInterpolation(points: DoubleArray, evaluations: DoubleArray) : String{

        val fx = StringBuilder()
        var pos : Boolean
        var xi = 0
        var xj = 0

        for (i in points.indices) {

            fx.append("((")
            pos = i == points.size - 1

            for (j in points.indices) {

                if (i != j) {

                    if (points.size > 1) {

                        if (j == points.size - 1) {

                            fx.append("(x-").append(points[xj]).append(")")

                        } else if (j < points.size) {

                            if (pos && j == i - 1) {

                                fx.append("(x-").append(points[xj]).append(")")

                            } else {

                                fx.append("(x-").append(points[xj]).append(")*")

                            }

                        }

                    } else {

                        fx.append("(x-").append(points[xj]).append(")")

                    }

                } else {


                }

                xj++

            }

            xj = 0

            for (j in points.indices) {

                if (i != j) {

                    if (points.size > 2) {

                        if (xi == 0 || j == 0) {

                            fx.append(")/((").append(points[i].toString()).append("-").append(points[xj]).append(")*")

                        } else if (j == points.size - 1) {

                            fx.append("(").append(points[i].toString()).append("-").append(points[xj]).append(")")

                        } else {

                            if (pos && j == i - 1) {

                                fx.append("(").append(points[i].toString()).append("-").append(points[xj]).append(")")

                            } else {

                                fx.append("(").append(points[i].toString()).append("-").append(points[xj]).append(")*")

                            }

                        }

                    } else {

                        fx.append(")/((").append(points[i].toString()).append("-").append(points[xj]).append(")")

                    }

                    xi++

                } else {


                }

                xj++

            }

            if (i != points.size - 1) {

                fx.append("))*").append(evaluations[i].toString()).append("+")

            } else {

                fx.append("))*").append(evaluations[i].toString())

            }
            xi = 0
            xj = 0

        }

        return simplify(fx.toString())

    }

    fun quadraticInterpolation(Intervals0: DoubleArray, Intervals1: DoubleArray, Intervals2: DoubleArray) : String {

        val x0 = Intervals0[0]
        val b0 = Intervals0[1]

        val x1 = Intervals1[0]
        val fx1 = Intervals1[1]
        val b1 = (fx1 - b0) / (x1 - x0)

        val x2 = Intervals2[0]
        val fx2 = Intervals2[1]
        val aux0 = (fx2 - fx1) / (x2 - x1)
        val aux1 = (fx1 - b0) / (x1 - x0)
        val b2 = (aux0 - aux1) / (x2 - x0)

        return simplify((b0.toString() + "+" + b1.toString() + "*(x-" + x0.toString() + ")+" + b2.toString() + "*(x-" +
                x0.toString() + ")*(x-" + x1.toString() + ")"))
    }

    fun evaluate(function: String, variable: String, value : Double) : Double {

        return try {

            val py: Python = Python.getInstance()
            val mathFunctions = py.getModule("MathFunctions")
            val x = mathFunctions.callAttr("Symbol", variable)
            val functionSymPy = mathFunctions.callAttr("sympify", function)
            val mathml = mathFunctions.callAttr("mathml",functionSymPy) to String()
            val functionParced = mathFunctions.callAttr("sympify", function).callAttr("subs", x,value) to Double
            val function2 = functionParced.first.repr()
            function2.toDouble()

        } catch (e : java.lang.Exception){

            NaN

        }

    }

    fun mathml(function: String) : ArrayList<String>{

        val ecuations : ArrayList<String> = ArrayList()

        return try{

            val py: Python = Python.getInstance()
            val mathFunctions = py.getModule("MathFunctions")
            val functionSymPy = mathFunctions.callAttr("sympify", function)
            val mathml = mathFunctions.callAttr("latex",functionSymPy) to String()
            var repr = mathml.first.repr()
            var cutted = repr.split("\\\\")
            repr = ""
            var i = 0
            for (part in cutted){

                if (i == cutted.size-1){

                    repr += part

                }else{

                    repr += "$part\\"
                    i++

                }

            }
            cutted = repr.split("'")
            repr = ""
            i = 0
            for (part in cutted){

                repr += part

            }
            ecuations.apply {

                add(0,function)
                add(1,repr)

            }

        } catch (e : Exception){

            ecuations

        }

    }

    private fun derive(function: String, respectTo : String) : String {

        return try{

            val py: Python = Python.getInstance()
            val mathFunctions = py.getModule("MathFunctions")
            val x = mathFunctions.callAttr("Symbol", respectTo)
            val derivative = mathFunctions.callAttr("diff",function,x) to String()
            derivative.first.repr()

        } catch(e : Exception) {

            ""

        }

    }

    fun simplify(function : String) : String{

        return try{

            val py: Python = Python.getInstance()
            val mathFunctions = py.getModule("MathFunctions")
            val derivative = mathFunctions.callAttr("simplify",function) to String()
            exponentiation(derivative.first.repr())

        }catch (e: Exception){

            function

        }

    }

    fun exponentiation(function: String) : String{

        val splitedFunction = function.split("**")
        var correctFunction = ""
        var i = 0
        for (part in splitedFunction){

            if (i == splitedFunction.size-1){

                correctFunction += part

            }else{

                correctFunction += "$part^"
                i++

            }

        }
        return correctFunction

    }

    fun solve(function: String, variable: String) : String{

        return try{

            val py: Python = Python.getInstance()
            val mathFunctions = py.getModule("MathFunctions")
            val x = mathFunctions.callAttr("Symbol", variable)
            val derivative = mathFunctions.callAttr("solve",function, x) to String()
            val fun1 = mathml(exponentiation(derivative.first.repr()))
            fun1[1]

        }catch (e: Exception){

            function

        }

    }

    fun leastSquares(Xs: DoubleArray, Ys: DoubleArray) : Array<String>{

        val xy = DoubleArray(Xs.size)
        val x2 = DoubleArray(Xs.size)

        var sumx = 0.0
        var sumy = 0.0
        var sumxy = 0.0
        var sumx2 = 0.0

        for (i in Xs.indices) {

            xy[i] = Xs[i] * Ys[i]
            x2[i] = Xs[i] * Xs[i]

        }

        for (i in Xs.indices) {

            sumx += Xs[i]
            sumy += Ys[i]
            sumxy += xy[i]
            sumx2 += x2[i]

        }

        if (sumx < 0) {

            sumx *= -1

        }

        val m = (Xs.size * sumxy - sumx * sumy) / (Xs.size * sumx2 - sumx * sumx)
        val b = (sumy * sumx2 - sumx * sumxy) / (Xs.size * sumx2 - sumx * sumx)

        val fx = "$m*x+$b"
        val fy = "(y-$b)/($m)"

        return arrayOf(simplify(fx),simplify(fy))

    }

    fun newtonRaphson(function: String, variable : String, intervals: DoubleArray, tolerance: Double, limit: Int) : ArrayList<Iteracion>{

        var numberIteration = 1
        var xn: Double = 0.toDouble()
        var stop = false
        var eval = 0.0
        val iteration: ArrayList<Iteracion> = ArrayList()

        while (!stop) {

            val it = Iteracion()

            if (numberIteration == 1) {

                val x = (intervals[0] + intervals[1]) / 2

                val functionO = evaluate(function, variable, x)
                val functionD = evaluate(derive(function, variable), variable, x)

                xn = x - functionO / functionD

                var functionR = evaluate(function, variable, xn)

                if (functionR < 0) {

                    functionR *= -1

                    if (tolerance > functionR) {

                        it.number = numberIteration
                        it.setXn(xn)
                        it.f = functionR
                        stop = true

                    } else {

                        it.number = numberIteration
                        it.setXn(xn)
                        it.f = functionR

                    }

                } else {

                    if (tolerance > functionR) {

                        it.number = numberIteration
                        it.setXn(xn)
                        it.f = functionR
                        stop = true

                    } else {

                        it.number = numberIteration
                        it.setXn(xn)
                        it.f = functionR

                    }
                }

                iteration.add(it)
                numberIteration = 2

            } else {

                val functionO = evaluate(function, variable, xn)
                val functionD = evaluate(derive(function, variable), variable, xn)

                xn -= functionO / functionD

                var functionR = evaluate(function, variable, xn)

                if (eval==functionR){

                    break

                }else{

                    eval= functionR

                }

                if (functionR < 0) {

                    functionR *= -1
                    if (tolerance > functionR) {

                        it.number = numberIteration
                        it.setXn(xn)
                        it.f = functionR
                        stop = true

                    } else {

                        it.number = numberIteration
                        it.setXn(xn)
                        it.f = functionR

                    }

                } else {

                    if (tolerance > functionR) {

                        it.number = numberIteration
                        it.setXn(xn)
                        it.f = functionR
                        stop = true

                    } else {

                        it.number = numberIteration
                        it.setXn(xn)
                        it.f = functionR

                    }
                }

                iteration.add(it)
                numberIteration++

            }

            if (limit>0){

                if (numberIteration>limit){

                    break

                }else if (numberIteration>250){

                    break

                }

            }else if (numberIteration>250){

                break

            }

        }

        return iteration

    }

    fun isFunction(function: String, variable: Char, value : Double) : Boolean {

        //Hibridacion Exitosa con Python
        return try {

            val py: Python = Python.getInstance()
            val mathFunctions = py.getModule("MathFunctions")
            val x = mathFunctions.callAttr("Symbol", variable)
            val functionParced = mathFunctions.callAttr("sympify", function).callAttr("subs", x,value) to String()
            false

        } catch (e : java.lang.Exception){

            true

        }

    }

}
