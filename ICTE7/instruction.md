ICTE5 的 加强版
Building a Bottom Navigation Drawing App
Objective
A BottomNavigationView app with 3 tabs

A Home tab

A Drawing tab (basic finger painting only)

A Settings tab

75% of the project will be scaffolded; you will complete TODO sections.

Step 1: Create a New Android Project
Step 2: Setup Project Structure
Create the following files:

Kotlin Classes:
MainActivity.kt

HomeFragment.kt

DrawingFragment.kt

SettingsFragment.kt

DrawingView.kt (Custom View)

Layouts:
activity_main.xml

fragment_home.xml

fragment_drawing.xml

fragment_settings.xml

menu/navigation_menu.xml

Gradle Dependency:
Make sure you have this in build.gradle (Module: app):

implementation 'com.google.android.material:material:1.7.0'
Sync your project.

Step 3: activity_main.xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <FrameLayout
        android:id="@+id/fragmentContainer"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_above="@id/bottomNavigationView" />

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottomNavigationView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        app:menu="@menu/navigation_menu" />

</RelativeLayout>
Step 4: navigation_menu.xml
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/nav_home"
        android:title="Home"
        android:icon="@android:drawable/ic_menu_info_details" />

    <item
        android:id="@+id/nav_drawing"
        android:title="Draw"
        android:icon="@android:drawable/ic_menu_edit" />

    <item
        android:id="@+id/nav_settings"
        android:title="Settings"
        android:icon="@android:drawable/ic_menu_preferences" />
</menu>
Step 5: MainActivity.kt (Scaffolded)
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        loadFragment(HomeFragment())

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_drawing -> loadFragment(DrawingFragment())
                R.id.nav_settings -> loadFragment(SettingsFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
Step 6: HomeFragment.kt (Done)
class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}
fragment_home.xml:

<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Welcome to Drawing App!"
        android:textSize="24sp" />
</LinearLayout>
Step 7: SettingsFragment.kt (Done)
class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
}
fragment_settings.xml:

<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Settings coming soon..."
        android:textSize="20sp" />
</LinearLayout>
Step 8: DrawingFragment.kt (Scaffolded)
class DrawingFragment : Fragment() {

    private lateinit var drawingView: DrawingView
    private lateinit var clearButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_drawing, container, false)

        drawingView = view.findViewById(R.id.drawingView)
        clearButton = view.findViewById(R.id.clearButton)

        clearButton.setOnClickListener {
            // TODO: Clear the drawing canvas
        }

        return view
    }
}
fragment_drawing.xml:

<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <Button
        android:id="@+id/clearButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Clear Drawing"
        android:layout_gravity="center" />

    <com.example.bottomnavdrawing.DrawingView
        android:id="@+id/drawingView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:background="@android:color/white"/>
</LinearLayout>
Step 9: DrawingView.kt (Use Existing Finger Painting Logic)
Provide students with basic DrawingView:

Handle touch events (ACTION_DOWN, ACTION_MOVE, ACTION_UP)

Draw paths with Paint

TODO: Add a method clearCanvas() that erases the drawing.

Rubric：
Proper Functionality of the App
50 pts Full Marks