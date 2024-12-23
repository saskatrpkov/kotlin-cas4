package com.example.todoapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentAddTodoPopupBinding
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.utils.ToDoData
import com.google.android.material.textfield.TextInputEditText

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddTodoPopupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddTodoPopupFragment : DialogFragment() {


    companion object{
        const val TAG = "AddTodoPopupFragment"
        @JvmStatic
        fun newInstance(taskId: String, task : String) =
            AddTodoPopupFragment().apply {
                arguments = Bundle().apply {
                    putString("taskId", taskId)
                    putString("task", task)
                }
            }
    }


    private lateinit var binding: FragmentAddTodoPopupBinding
    private lateinit var listener: DialogNextBtnClickListener
    private var toDoData : ToDoData? = null

    fun setListener(listener: DialogNextBtnClickListener){
        this.listener=listener
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentAddTodoPopupBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null){
            toDoData = ToDoData(arguments?.getString("taskId").toString(),
                arguments?.getString("task").toString())
        }
        binding.todoEt.setText(toDoData?.task)
        registerEvents()

    }

    private fun registerEvents() {
        binding.todoNextBtn.setOnClickListener{
            val todoTask = binding.todoEt.text.toString().trim()
            if(todoTask.isNotEmpty()){
                if(toDoData == null){
                    listener.onSaveTask(todoTask, binding.todoEt)
                } else {
                    toDoData?.task = todoTask
                    listener.onUpdateTask(toDoData!!, binding.todoEt)
                }
            } else {
                Toast.makeText(context, "Please type some task",
                    Toast.LENGTH_SHORT).show()
            }
        }
        binding.todoClose.setOnClickListener {
            dismiss()
        }
    }



    interface DialogNextBtnClickListener{
        fun onSaveTask(todo:String,todoEt:TextInputEditText)
        fun onUpdateTask(toDoData: ToDoData, todoEt: TextInputEditText)
    }

}