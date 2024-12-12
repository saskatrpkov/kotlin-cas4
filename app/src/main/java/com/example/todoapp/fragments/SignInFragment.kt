package com.example.todoapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentSignInBinding
import com.example.todoapp.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignInFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_sign_in, container, false)
        binding= FragmentSignInBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvents()
    }

    private fun init(view: View){
        navController= Navigation.findNavController(view)
        auth=FirebaseAuth.getInstance()
    }

    private fun registerEvents(){
        binding.authTextView.setOnClickListener {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.nextBtn.setOnClickListener {
            val email=binding.emailEt.text.toString().trim()
            val pass=binding.passEt.text.toString().trim()

            if(email.isNotEmpty() && pass.isNotEmpty() ){
                    binding.progressBar.visibility=View.VISIBLE
                    auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
                        if(it.isSuccessful){
                            navController.navigate(R.id.action_signInFragment_to_homeFragment)
                            Toast.makeText(context,"Login successfully!", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(context,it.exception?.message, Toast.LENGTH_SHORT)
                        }
                        binding.progressBar.visibility=View.GONE
                    }

            }else{
                Toast.makeText(context,"Empty fields are not allowed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}