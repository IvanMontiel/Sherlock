package mx.brennen.sherlock

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.multidex.MultiDex
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.android.synthetic.main.activity_home.*
import mx.brennen.sherlock.res.Item
import mx.brennen.sherlock.res.PersonalAdapter
import mx.brennen.sherlock.res.misc.OnNoteLister
import mx.brennen.sherlock.res.misc.TypefaceUtil
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast

@Suppress("UNCHECKED_CAST")
class HomeActivity : FragmentActivity() ,OnNoteLister{

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun OnNoteListener(position: Int) {

        when(itemList[position].title){

            "Metodos de Intervalo" -> {

                views.clear()
                itemList.clear()
                itemList.add(Item("Metodo de Valor Intermedio", "Metodo de Interpolacion Lineal",R.drawable.curlybrackets))
                itemList.add(Item("Metodo de Newton-Raphson", "Metodo de Interpolacion Lineal",R.drawable.graph))
                itemList.add(Item("Metodo de la Secante", "Metodo de Interpolacion Lineal",R.drawable.curlybrackets))
                itemList.add(Item("Método de Falsa posicion", "Metodo de Interpolacion Lineal",R.drawable.bucle))
                itemList.add(Item("Metodo de Punto Fijo", "Metodo de Interpolacion Lineal",R.drawable.curlybrackets))

                personalAdapter = PersonalAdapter(itemList,this, Typeface.createFromAsset(assets,"fonts/arciform.otf"))
                recyclerView.adapter = personalAdapter

            }

            "Método de Falsa posicion" -> {

                /*val extras = FragmentNavigatorExtras(
                    personalAdapter.images[position] to "itemImage" )*/
                homeAct.closeDrawers()
                views = personalAdapter.images
                val newFragment = FalseFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.replace(R.id.FragmentHost, newFragment,"Falsa Posicion")
                transaction.addSharedElement(views[position],  "itemImage")
                transaction.addToBackStack(null)
                transaction.commit()

            }

            "Metodo de Valor Intermedio" -> {

                homeAct.closeDrawers()
                val newFragment = IntermediateFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.replace(R.id.FragmentHost, newFragment,"Intermedio")
                transaction.addToBackStack(null)
                transaction.commit()

            }

            "Metodo de Newton-Raphson" -> {

                homeAct.closeDrawers()
                val newFragment = NewtonFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.replace(R.id.FragmentHost, newFragment,"Intermedio")
                transaction.addToBackStack(null)
                transaction.commit()

            }

            "Metodo de la Secante" -> {

                homeAct.closeDrawers()
                val newFragment = SecantFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.replace(R.id.FragmentHost, newFragment,"Intermedio")
                transaction.addToBackStack(null)
                transaction.commit()

            }

            "Metodo de Punto Fijo" -> {

                homeAct.closeDrawers()
                val newFragment = FixedFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.replace(R.id.FragmentHost, newFragment,"Intermedio")
                transaction.addToBackStack(null)
                transaction.commit()

            }

        }

        toast("Hola")

    }

    private lateinit var personalAdapter : PersonalAdapter
    private var itemList : ArrayList<Item> = ArrayList()
    private var views : ArrayList<ImageView> = ArrayList()
    private lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        MultiDex.install(baseContext)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        actionBar?.hide()

        if(!Python.isStarted()) {

            Python.start(AndroidPlatform(applicationContext))
            //Precarga de librerias
            val py: Python = Python.getInstance()
            val mathFunctions = py.getModule("MathFunctions")

        }

        TypefaceUtil().overrideFont(baseContext,"SERIF","fonts/arciform.otf")

        var newFragment = ConfigurationFragment()
        var transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.replace(R.id.FragmentHost, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()

        itemList.clear()

        recyclerView = List
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        createList()

        personalAdapter = PersonalAdapter(itemList,this, Typeface.createFromAsset(assets,"fonts/arciform.otf"))
        views = personalAdapter.images
        recyclerView.adapter = personalAdapter

        val drawerToggle = object : ActionBarDrawerToggle(
            this,
            homeAct,0,0) {
            override fun onDrawerClosed(view: View) {

                itemList.clear()
                views.clear()

                recyclerView = List
                recyclerView.layoutManager = LinearLayoutManager(applicationContext)

                createList()

            }

            override fun onDrawerOpened(drawerView: View) {



            }
        }

        homeAct.addDrawerListener(drawerToggle)

        configuration.onClick {

            homeAct.closeDrawers()
            newFragment = ConfigurationFragment()
            transaction = supportFragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.replace(R.id.FragmentHost, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }

    }

    private fun createList() {

        itemList.add(Item("Metodos de Intervalo", "Metodo de Interpolacion Lineal",R.drawable.curlybrackets))
        itemList.add(Item("Metodos de Interpolación", "Metodo de Interpolacion Lineal",R.drawable.graph))
        itemList.add(Item("Métodos Iterativos", "Metodo de Interpolacion Lineal",R.drawable.bucle))
        itemList.add(Item("Diferenciacion Numérica", "Metodo de Interpolacion Lineal",R.drawable.partialderivative))
        itemList.add(Item("Integración Numérica", "Metodo de Interpolacion Lineal",R.drawable.integral))
        personalAdapter = PersonalAdapter(itemList,this, Typeface.createFromAsset(assets,"fonts/arciform.otf"))
        recyclerView.adapter = personalAdapter

    }

}
