/*------------------------------------------------------------------------------
 - Copyright (c) 2018. This code was created by Younes Charfaoui in the process of Graduation Project for the year of  2018 , which is about creating a platform  for students and professors to help them in the communication and the get known of the university information and so on.
 -----------------------------------------------------------------------------*/

package com.ibnkhaldoun.studentside.interfaces;


import com.ibnkhaldoun.studentside.networking.models.Response;


public interface SignUpTaskListener {
    void onSignUpCompletionListener(Response response);
}
