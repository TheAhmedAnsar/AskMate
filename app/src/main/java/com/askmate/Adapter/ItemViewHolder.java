//package com.askmate.Adapter;
//
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.widget.AppCompatImageView;
//import androidx.appcompat.widget.LinearLayoutCompat;
//import androidx.appcompat.widget.PopupMenu;
//
//import com.askmate.Modal.Question;
//import com.askmate.R;
//import com.google.android.material.imageview.ShapeableImageView;
//import com.google.firebase.auth.FirebaseAuth;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public  class ItemViewHolder  extends QnA_Adapter.ViewHolder {
//
//                TextView name, date, question, like, comments, category;
//                CircleImageView profile;
//                ShapeableImageView questionImage;
//                AppCompatImageView likeDislike, menuImage;
//                LinearLayoutCompat commentLayout;
//
//                public ItemViewHolder (@NonNull View itemView) {
//                    super(itemView);
//
//                    name = itemView.findViewById(R.id.name);
//                    date = itemView.findViewById(R.id.date);
//                    question = itemView.findViewById(R.id.question);
//                    category = itemView.findViewById(R.id.category);
//                    like = itemView.findViewById(R.id.likescount);
//                    comments = itemView.findViewById(R.id.commentscount);
//                    profile = itemView.findViewById(R.id.profile_image);
//                    questionImage = itemView.findViewById(R.id.imageQuestion);
//                    likeDislike = itemView.findViewById(R.id.LikeDislike);
//                    commentLayout = itemView.findViewById(R.id.layoutComment);
//                    menuImage = itemView.findViewById(R.id.menuItem);
//
//                }
//
//                public void bind(Question questionData, QnA_Adapter.OnItemActionListener itemActionListener) {
//                    if (questionData.getUid().equals(FirebaseAuth.getInstance().getUid())) {
//                        // Condition to check if the UID matches
//                        menuImage.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                PopupMenu popup = new PopupMenu(view.getContext(), view);
//                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                                    @Override
//                                    public boolean onMenuItemClick(MenuItem item) {
//                                        switch (item.getItemId()) {
//                                            case R.id.report:
//                                                Toast.makeText(view.getContext(), "Report", Toast.LENGTH_LONG).show();
//                                                return true;
//                                            case R.id.delete:
//                                                Toast.makeText(view.getContext(), "Delete operation", Toast.LENGTH_LONG).show();
//                                                itemActionListener.onDeleteItem(questionData);
//                                                return true;
//                                            default:
//                                                return false;
//                                        }
//                                    }
//                                });
//                                popup.inflate(R.menu.questionmenu);
//                                popup.show();
//                            }
//                        });
//
//                    } else {
//                        menuImage.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                PopupMenu popup = new PopupMenu(view.getContext(), view);
//                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                                    @Override
//                                    public boolean onMenuItemClick(MenuItem item) {
//                                        switch (item.getItemId()) {
//                                            case R.id.report:
//                                                Toast.makeText(view.getContext(), "Report", Toast.LENGTH_LONG).show();
//                                                return true;
//                                            case R.id.delete:
//                                                Toast.makeText(view.getContext(), "Delete", Toast.LENGTH_LONG).show();
//                                                return true;
//                                            default:
//                                                return false;
//                                        }
//                                    }
//                                });
//                                popup.inflate(R.menu.questionmenu);
//
//                                // Remove the delete menu item if UID matches
//                                Menu menu = popup.getMenu();
//                                MenuItem deleteItem = menu.findItem(R.id.delete);
//                                if (deleteItem != null) {
//                                    menu.removeItem(R.id.delete);
//                                }
//
//                                popup.show();
//                            }
//                        });
//                    }
//                }
//            }
