package com.codingwithme.notesapp

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.codingwithme.notesapp.adapter.NotesAdapter
import com.codingwithme.notesapp.database.NotesDatabase
import com.codingwithme.notesapp.entities.Notes
import kotlinx.android.synthetic.main.fragment_create_note.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : BaseFragment() {

    var arrNotes = ArrayList<Notes>()
    var notesAdapter: NotesAdapter = NotesAdapter()
    lateinit var noootes: List<Notes>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.setHasFixedSize(true)

        recycler_view.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)

        launch {
            context?.let {
                var notes = NotesDatabase.getDatabase(it).noteDao().getAllNotes()
                notesAdapter!!.setData(notes)
                arrNotes = notes as ArrayList<Notes>
                recycler_view.adapter = notesAdapter
            }
        }

        notesAdapter!!.setOnClickListener(onClicked)

        fabBtnCreateNote.setOnClickListener {
            replaceFragment(CreateNoteFragment.newInstance(),false)
        }

        alphaSortBtn.setOnClickListener {
            var tempArr = ArrayList<Notes>()
            arrNotes.sortedBy { it.title }.forEach{tempArr.add(it)}
            notesAdapter.setData(tempArr)
            notesAdapter.notifyDataSetChanged()

        }

        dateSortBtn.setOnClickListener {

            var tempArr = ArrayList<Notes>()



            val pattern = DateTimeFormatter.ofPattern(("dd/M/yyyy hh:mm:ss"))
            arrNotes.sortedBy {
                notes -> LocalDate.parse(notes.dateTime, pattern)

            }.forEach{tempArr.add(it)}
            notesAdapter.setData(tempArr)
            notesAdapter.notifyDataSetChanged()
            }

        search_view.setOnQueryTextListener( object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                var tempArr = ArrayList<Notes>()

                for (arr in arrNotes){
                    if (arr.title!!.toLowerCase(Locale.getDefault()).contains(p0.toString())){
                        tempArr.add(arr)
                    }
                }

                notesAdapter.setData(tempArr)
                notesAdapter.notifyDataSetChanged()
                return true
            }

        })


    }


    private val onClicked = object :NotesAdapter.OnItemClickListener{
        override fun onClicked(notesId: Int) {


            var fragment :Fragment
            var bundle = Bundle()
            bundle.putInt("noteId",notesId)
            fragment = CreateNoteFragment.newInstance()
            fragment.arguments = bundle

            replaceFragment(fragment,false)
        }

    }


    fun replaceFragment(fragment:Fragment, istransition:Boolean){
        val fragmentTransition = activity!!.supportFragmentManager.beginTransaction()

        if (istransition){
            fragmentTransition.setCustomAnimations(android.R.anim.slide_out_right,android.R.anim.slide_in_left)
        }
        fragmentTransition.replace(R.id.frame_layout,fragment).addToBackStack(fragment.javaClass.simpleName).commit()
    }


}