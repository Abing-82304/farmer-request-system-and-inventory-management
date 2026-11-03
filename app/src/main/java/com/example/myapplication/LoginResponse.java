package com.example.myapplication;

public class LoginResponse {


        private boolean success;
        private String message;
        private User user;

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public User getUser() {
            return user;
        }

        public static class User {

            private int id;
            private String username;
            private String role;
            private String status;

            public int getId() {
                return id;
            }

            public String getUsername() {
                return username;
            }

            public String getRole() {
                return role;
            }

            public String getStatus() {
                return status;
            }
        }
    }

