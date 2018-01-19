package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import org.parceler.Parcels.wrap


class MainActivity : AppCompatActivity(), ContentAdapter.OnItemClickListener {
    private val TAG = MainActivity::class.java.simpleName

    private val listItemsDHT: ArrayList<MyData> = arrayListOf()
    private val listItemsSEN: ArrayList<MyData> = arrayListOf()
    private val listItemsUpdate: ArrayList<MyData> = arrayListOf()

    private lateinit var contentAdapter: ContentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contentAdapter = ContentAdapter(listItemsUpdate, this)
        list.adapter = contentAdapter
        list.layoutManager = GridLayoutManager(this, 3)

        val database = FirebaseDatabase.getInstance()
        val myRef1 = database.getReference("logDHT")
        myRef1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listItemsDHT.clear()
                dataSnapshot.children.mapNotNullTo(listItemsDHT) {
                    var item = it.getValue<LogDHT>(LogDHT::class.java)
                    item?.id = it.key
                    item
                }

                updateResult()

                Log.d(TAG, "Value size DHT: " + listItemsDHT.size)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value DHT.", error.toException())
            }
        })

        val myRef2 = database.getReference("logSEN")
        myRef2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listItemsSEN.clear()

                dataSnapshot.children.mapNotNullTo(listItemsSEN) {
                    var item = it.getValue<LogSEN>(LogSEN::class.java)
                    item?.id = it.key
                    item
                }

                updateResult()

                Log.d(TAG, "Value size SEN: " + listItemsSEN.size)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value SEN.", error.toException())
            }
        })
    }

    fun updateResult() {
        listItemsUpdate.clear()
        listItemsUpdate.addAll(listItemsDHT)
        listItemsUpdate.addAll(listItemsSEN)

        contentAdapter.notifyDataSetChanged()
    }

    override fun onItemClickListener(content: MyData) {
        Log.d(TAG, content.toString())

        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("content", wrap(content))
        startActivity(intent)
    }
}
