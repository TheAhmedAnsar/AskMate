package com.askmate.roughRecyclerview;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.askmate.Modal.Question;
import com.askmate.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {
	private boolean isLoading = false;
	private String lastKey = null;
	private int count = 0;
	ArrayList<CourseModal> newCourses;

	private final int PAGE_SIZE = 10;
	// creating variables for our UI components.
//	int count = 0;
	String url = "https://jsonkeeper.com/b/CXSL";
	private ArrayList<Question> courseArrayList;
	private RecyclerView courseRV;
	private CourseRVAdapter courseRVAdapter;
	private ProgressBar loadingPB;
	private NestedScrollView nestedSV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newactivity);
		
		// initializing our variables.
		courseRV = findViewById(R.id.idRVCourses);
		loadingPB = findViewById(R.id.idPBLoading);
		nestedSV = findViewById(R.id.idNestedSV);
		 newCourses = new ArrayList<>();

		// initializing our array list.
		courseArrayList = new ArrayList<>();
		
		// calling a method to add data to our array list.
//		getData();
		
		// on below line we are setting layout manager to our recycler view.
		LinearLayoutManager manager = new LinearLayoutManager(this);
		courseRV.setLayoutManager(manager);
		courseRVAdapter = new CourseRVAdapter(this, newCourses);
		courseRV.setAdapter(courseRVAdapter);
		loadinitialdata();

		loadingPB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String id = newCourses.get(newCourses.size()-1).getCourseName();
				Toast.makeText(MainActivity.this, id, Toast.LENGTH_SHORT).show();
				loadmoredata(id);

			}
		});
//		DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("askmate").child("questions");
//		Query initialQuery = databaseReference.orderByKey().limitToLast(10); // Load 10 latest items
//
//

//		initialQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//			@Override
//			public void onDataChange(DataSnapshot dataSnapshot) {
////				List<YourDataType> dataList = new ArrayList<>();
//				String lastKey = null;
//				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
////					YourDataType item = snapshot.getValue(YourDataType.class);
//					String courseName = snapshot.child("question").getValue(String.class);
////					courseArrayList.;
//					lastKey = snapshot.getKey();
//				}
//				// Reverse the list to get the latest first
//				Collections.reverse(courseArrayList);
//
//				// Update your RecyclerView or Adapter here with dataList
//
//				// Store the last key for next query
//				if (courseArrayList.size() > 0) {
//					lastKey = courseArrayList.get(courseArrayList.size() - 1).getQuestionId();
//				}
//			}
//
//			@Override
//			public void onCancelled(DatabaseError databaseError) {
//				// Handle possible errors.
//			}
//		});

		// adding on scroll change listener method for our nested scroll view.
//		nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//			@Override
//			public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//				// on scroll change we are checking when users scroll as bottom.
//				if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
//					// in this method we are incrementing page number,
//					// making progress bar visible and calling get data method.
//					count++;
//					// on below line we are making our progress bar visible.
//					loadingPB.setVisibility(View.VISIBLE);
//					if (count < 20) {
//						// on below line we are again calling
//						// a method to load data in our array list.
//						Toast.makeText(MainActivity.this, "Im running", Toast.LENGTH_SHORT).show();
//						getData();
//					}
//				}
//			}
//		});

//		loadData();

		// Adding scroll change listener for pagination
//		nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//			@Override
//			public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//				if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() && !isLoading) {
//					count++;
//					loadingPB.setVisibility(View.VISIBLE);
//					loadData();
//				}
//			}
//		});
	}

	private void loadmoredata(String id) {
		Log.d("id", "Size: " + newCourses.size());

		ArrayList<CourseModal> newCourses = new ArrayList<>();

		DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("askmate").child("questions");

		Query initialQuery = databaseReference.orderByKey().endAt(id).limitToLast(11); // Load 10 latest items



		initialQuery.addListenerForSingleValueEvent(new ValueEventListener() {
	@Override
	public void onDataChange(@NonNull DataSnapshot snapshot) {
		for (DataSnapshot snapshot1: snapshot.getChildren())
		{
			String question = snapshot1.getKey();
			Log.d("id", "onDataChange: " + question);
//			if (C).equals(lastKey)) { // Exclude the last item from the previous query
//				dataList.add(data);



			if ( !question.equals(id)) {
				newCourses.add(new CourseModal(question, "", "", ""));
			}
//			newCourses.add(new CourseModal(question, "", "", ""));

//					lastKey = s
		}
		Collections.reverse(newCourses);
		courseRVAdapter.addData(newCourses);

		lastKey = newCourses.get(newCourses.size()-1).getCourseName();
		Log.d("id", "Lastkey---->: " + lastKey);

	}



	@Override
	public void onCancelled(@NonNull DatabaseError error) {

	}
});
	}


	private void loadinitialdata()
	{

		DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("askmate").child("questions");
		Query initialQuery = databaseReference.orderByKey().limitToLast(10); // Load 10 latest items

		initialQuery.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				for (DataSnapshot snapshot1: snapshot.getChildren())
				{
					String question = snapshot1.getKey();
					newCourses.add(new CourseModal(question, "", "", ""));

//					lastKey = s
				}
				Collections.reverse(newCourses);
				courseRVAdapter.addData(newCourses);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});

	}


//	private void loadData() {
//		isLoading = true;
//
//		DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("askmate")
//				.child("questions");
//		Query query;
//
//		if (lastKey == null) {
//			Toast.makeText(MainActivity.this, "ONE", Toast.LENGTH_SHORT).show();
//
//
//			query = databaseReference.orderByKey().limitToLast(PAGE_SIZE);
//		} else {
//			Toast.makeText(MainActivity.this, "TWO", Toast.LENGTH_SHORT).show();
//
//			query = databaseReference.orderByKey().endAt(lastKey).limitToLast(PAGE_SIZE + 1);
//		}
//
//		query.addListenerForSingleValueEvent(new ValueEventListener() {
//			@Override
//			public void onDataChange(@NonNull DataSnapshot snapshot) {
//				if (snapshot.exists()) {
//					ArrayList<CourseModal> newCourses = new ArrayList<>();
//					String newLastKey = null;
//
//					for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//						if (newLastKey == null || !snapshot1.getKey().equals(lastKey)) {
//							String courseName = snapshot1.child("question").getValue(String.class);
//							newCourses.add(new CourseModal(courseName, "", "", ""));
//							newLastKey = snapshot1.getKey();
//						}
//					}
//
//
//				courseRVAdapter = new CourseRVAdapter(MainActivity.this, courseArrayList);
//				courseRV.setAdapter(courseRVAdapter);
//					if (lastKey != null && !newCourses.isEmpty()) {
//						newCourses.remove(0); // Remove the duplicate last key element
//					}
//
//					if (!newCourses.isEmpty()) {
//						courseArrayList.addAll(newCourses);
//						lastKey = newLastKey;
//						courseRVAdapter.notifyDataSetChanged();
//					}
//				}
//
//				isLoading = false;
//				loadingPB.setVisibility(View.GONE);
//			}
//
//			@Override
//			public void onCancelled(@NonNull DatabaseError error) {
//				Log.e("FirebaseError", "Error: " + error.getMessage());
//				Toast.makeText(MainActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
//				isLoading = false;
//				loadingPB.setVisibility(View.GONE);
//			}
//		});
//
////	private void getData() {
////
////		DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("askmate")
////				.child("questions");
////		databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
////			@Override
////			public void onDataChange(@NonNull DataSnapshot snapshot) {
////				for (DataSnapshot snapshot1 : snapshot.getChildren()) {
////					String courseName = snapshot1.child("question").getValue(String.class);
////
////					courseArrayList.add(new CourseModal(courseName, "", "", ""));
////				}
////				// Initialize and set the adapter
////				courseRVAdapter = new CourseRVAdapter(MainActivity.this, courseArrayList);
////				courseRV.setAdapter(courseRVAdapter);
////			}
////
////			@Override
////			public void onCancelled(@NonNull DatabaseError error) {
////
////			}
////		});
////	}
//		// creating a new variable for our request queue
//
////		RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
////
////
////		// in this case the data we are getting is in the form
////		// of array so we are making a json array request.
////		// below is the line where we are making an json array
////		// request and then extracting data from each json object.
////		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
////			@Override
////			public void onResponse(JSONArray response) {
////
////
////				courseRV.setVisibility(View.VISIBLE);
////				for (int i = 0; i < response.length(); i++) {
////					// creating a new json object and
////					// getting each object from our json array.
////					try {
////						// we are getting each json object.
////						JSONObject responseObj = response.getJSONObject(i);
////
////						// now we get our response from API in json object format.
////						// in below line we are extracting a string with
////						// its key value from our json object.
////						// similarly we are extracting all the strings from our json object.
////						String courseName = responseObj.getString("courseName");
////						String courseTracks = responseObj.getString("courseTracks");
////						String courseMode = responseObj.getString("courseMode");
////						String courseImageURL = responseObj.getString("courseimg");
////						courseArrayList.add(new CourseModal(courseName, courseMode, courseTracks, courseImageURL));
////
////						// on below line we are adding our array list to our adapter class.
////						courseRVAdapter = new CourseRVAdapter(MainActivity.this, courseArrayList);
////
////						// on below line we are setting
////						// adapter to our recycler view.
////						courseRV.setAdapter(courseRVAdapter);
////					} catch (JSONException e) {
////						e.printStackTrace();
////					}
////				}
////			}
////		}, new Response.ErrorListener() {
////			@Override
////			public void onErrorResponse(VolleyError error) {
////				Toast.makeText(MainActivity.this, "Fail to get the data..", Toast.LENGTH_SHORT).show();
////			}
////		});
////		queue.add(jsonArrayRequest);
//	}
}
