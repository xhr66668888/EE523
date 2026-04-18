package com.example.todolist

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class TaskItem {
    var TheName: String = ""
    var T_deadline: String = "empty"
    var flagDone: Boolean = false
}

class task_adapter(var ct: Context, var arr: ArrayList<TaskItem>, var myCounter: TextView) : RecyclerView.Adapter<task_adapter.v_holder>() {

    class v_holder(v: View) : RecyclerView.ViewHolder(v) {
        var word = v.findViewById<TextView>(R.id.taskText1)
        var deadline = v.findViewById<TextView>(R.id.timeStr)
        var box = v.findViewById<CheckBox>(R.id.Check2)
        var b1 = v.findViewById<Button>(R.id.Del)
        var b2 = v.findViewById<Button>(R.id.edit_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): v_holder {
       // LayoutInflater inflater = LayoutInflater.from(ct);
        var vv = LayoutInflater.from(ct).inflate(R.layout.item_task, parent, false)
        return v_holder(vv)
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    override fun onBindViewHolder(holder: v_holder, position: Int) {
        var node = arr.get(position)
        holder.word.text = node.TheName
        holder.deadline.text = node.T_deadline
        
        holder.box.isChecked = node.flagDone

        if (node.flagDone) {
            holder.word.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        } else {
             holder.word.paintFlags = 0
            // holder.word.paintFlags = Paint.LINEAR_TEXT_FLAG
        }

        holder.box.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (holder.box.isChecked) {
                    node.flagDone = true
                    holder.word.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    println("checked on!")
                } else {
                    node.flagDone = false
                    holder.word.paintFlags = 0 // back to normal
                    println("checked off!")
                }
            }
        })

        holder.b1.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                println("del hit")
                arr.removeAt(currentPosition)
                notifyItemRemoved(currentPosition)
                notifyItemRangeChanged(currentPosition, arr.size)
                
                val str = arr.size.toString()
                myCounter.text = "$str Tasks remaining"
            }
        }

        holder.b2.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                println("edit hit")
                val nodeToEdit = arr[currentPosition]
                
                val alert = AlertDialog.Builder(ct)
                alert.setTitle("Edit:")
                
                val box2 = EditText(ct)
                box2.setText(nodeToEdit.TheName)
                alert.setView(box2)
                
                alert.setPositiveButton("ok") { dialog, which ->
                    nodeToEdit.TheName = box2.text.toString()
                    
                    val pick = TimePickerDialog(ct, AlertDialog.THEME_HOLO_LIGHT, { view, h, m ->
                        val mStr = if (m < 10) "0$m" else m.toString()
                        nodeToEdit.T_deadline = "$h:$mStr"
                        notifyItemChanged(currentPosition)
                    }, 12, 0, true)
                    pick.show()
                }
                alert.show()
            }
        }
    }
}

class MainActivity : AppCompatActivity() {

    var myTasks = ArrayList<TaskItem>()
    var adapterMain: task_adapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println("main")

        var btop = findViewById<Button>(R.id.AddBTN)
        var input_t = findViewById<EditText>(R.id.etAdd)
        var counterTxt = findViewById<TextView>(R.id.count_view)
        var RVIEW = findViewById<RecyclerView>(R.id.my_recycler)

        adapterMain = task_adapter(this, myTasks, counterTxt)
        RVIEW.layoutManager = LinearLayoutManager(this)
        RVIEW.adapter = adapterMain

        btop.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var string1 = input_t.text.toString()
                
                if (string1.length > 0) {
                    println("getting time...")
                    val dialog = TimePickerDialog(this@MainActivity, AlertDialog.THEME_HOLO_LIGHT, { v2, h, m ->
                        var temp = TaskItem()
                        temp.TheName = string1
                        
                        var mns = m.toString()
                        if(m < 10){
                            mns = "0" + m // fix
                        }
                        
                        temp.T_deadline = h.toString() + ":" + mns
                        
                        myTasks.add(temp)
                        adapterMain!!.notifyDataSetChanged()
                        
                        input_t.setText("")
                        var s = myTasks.size.toString()
                        counterTxt.text = s + " Tasks remaining"
                        
                    }, 12, 0, true)
                    dialog.show()
                    
                } else {
                    println("error")
                }
            }
        })
    }
}
