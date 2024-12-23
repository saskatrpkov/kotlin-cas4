package com.example.todoapp.fragments

import android.os.Bundle
import android.view.*
import android.widget.Adapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.databinding.FragmentSignUpBinding
import com.example.todoapp.utils.ToDoAdapter
import com.example.todoapp.utils.ToDoData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(),AddTodoPopupFragment.DialogNextBtnClickListener, ToDoAdapter.ToDoAdapterClicksInterface {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var databaseRef:DatabaseReference
    private lateinit var binding: FragmentHomeBinding
    private var popUpFragment: AddTodoPopupFragment? = null
    private lateinit var adapter: ToDoAdapter
    private lateinit var mList: MutableList<ToDoData>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_home, container, false)
        binding=FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        setHasOptionsMenu(true)
        registerEvents()
        getDataFromFirebase()
    }

    private fun init(view: View){
        navController= Navigation.findNavController(view)
        auth=FirebaseAuth.getInstance()
        databaseRef=FirebaseDatabase.getInstance("https://mpos-1910e-default-rtdb.firebaseio.com").reference.child("Tasks").child(auth.currentUser?.uid.toString())
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager=LinearLayoutManager(context)
        mList= mutableListOf()
        adapter= ToDoAdapter(mList)
        adapter.setListener(this)
        binding.recyclerView.adapter=adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId==R.id.logout){
            auth.signOut()
            navController.navigate(R.id.action_homeFragment_to_signInFragment)
            return true
        }
        return false
    }

    private fun registerEvents() {
        binding.addBtnHome.setOnClickListener {
            if(popUpFragment != null){
                childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
            }
            popUpFragment = AddTodoPopupFragment()
            popUpFragment!!.setListener(this)
            popUpFragment!!.show(childFragmentManager, AddTodoPopupFragment.TAG)
        }
    }



    override fun onSaveTask(todo: String, todoEt: TextInputEditText) {
        databaseRef.push().setValue(todo).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText( context,"Todo saved successfully", Toast.LENGTH_SHORT).show()
                todoEt.text=null
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            popUpFragment!!.dismiss()
        }

    }

    override fun onUpdateTask(toDoData: ToDoData, todoEt: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[toDoData.taskId] = toDoData.task
        databaseRef.updateChildren(map).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context, "Updated successfully!",
                    Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message,
                    Toast.LENGTH_SHORT).show()
            }
            todoEt.text = null
            popUpFragment!!.dismiss()
        }

    }

    private fun getDataFromFirebase(){
        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (taskSnapshot in snapshot.children){
                    val todoTask=taskSnapshot.key?.let {
                        ToDoData(it,taskSnapshot.value.toString())
                    }
                    if(todoTask!=null){
                        mList.add(todoTask)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
            }

        })
    }
    override fun onDeleteTaskBtnClicked(toDoData: ToDoData) {
        databaseRef.child(toDoData.taskId).removeValue().addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context,"Deleted successfully", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditTaskBtnClicked(toDoData: ToDoData) {
        if(popUpFragment != null){
            childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
            popUpFragment = AddTodoPopupFragment.newInstance(toDoData.taskId,
                toDoData.task)
            popUpFragment!!.setListener(this)
            popUpFragment!!.show(childFragmentManager, AddTodoPopupFragment.TAG)
        }
    }
}