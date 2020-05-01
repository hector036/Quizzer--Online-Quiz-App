

 myRef.child("Users").child(Objects.requireNonNull(mAuth.getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        }else {
                            Intent editProfileIntent = new Intent(SplashActivity.this, EditProfileActivity.class);
                            editProfileIntent.putExtra("type",1);
                            editProfileIntent.putExtra("phone",mAuth.getCurrentUser().getPhoneNumber());
                            startActivity(editProfileIntent);
                            finish();
                        }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });