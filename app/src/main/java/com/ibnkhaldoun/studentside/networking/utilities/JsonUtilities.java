package com.ibnkhaldoun.studentside.networking.utilities;

import com.ibnkhaldoun.studentside.models.Professor;
import com.ibnkhaldoun.studentside.models.Student;
import com.ibnkhaldoun.studentside.models.Subject;
import com.ibnkhaldoun.studentside.networking.models.ForgetPasswordResponse;
import com.ibnkhaldoun.studentside.networking.models.LoginResponse;
import com.ibnkhaldoun.studentside.networking.models.SignUpResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.JSON_PROFESSOR_DEGREE;
import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.JSON_PROFESSOR_EMAIL;
import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.JSON_PROFESSOR_FIRST_NAME;
import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.JSON_PROFESSOR_ID;
import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.JSON_PROFESSOR_LAST_NAME;
import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.JSON_STUDENT_AVERAGE;
import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.JSON_STUDENT_CONFIRMED;
import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.JSON_STUDENT_EMAIL;
import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.JSON_STUDENT_FIRST_NAME;
import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.JSON_STUDENT_GROUP;
import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.JSON_STUDENT_ID;
import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.JSON_STUDENT_LAST_NAME;
import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.JSON_STUDENT_LEVEL;
import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.JSON_STUDENT_SECTION;
import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.KEY_JSON_DATA;
import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.KEY_JSON_STATUS;
import static com.ibnkhaldoun.studentside.providers.KeyDataProvider.NETWORK_SUCCESS;

/**
 * this class will make the role of doing the parse
 * process for all the responses.
 */

public class JsonUtilities {

    public static LoginResponse getLoginResponse(String jsonText) {
        try {
            LoginResponse response = new LoginResponse();

            JSONObject root = new JSONObject(jsonText);
            int status = Integer.parseInt(root.getString(KEY_JSON_STATUS));
            response.setStatus(status);

            if (status == NETWORK_SUCCESS) {

                JSONObject data = root.getJSONObject(KEY_JSON_DATA);

                if (data.has(JSON_STUDENT_ID)) {
                    Student student = new Student();
                    student.setId(data.getString(JSON_STUDENT_ID));
                    student.setFirstName(data.getString(JSON_STUDENT_FIRST_NAME));
                    student.setLastName(data.getString(JSON_STUDENT_LAST_NAME));
                    student.setAverage(Float.parseFloat(data.getString(JSON_STUDENT_AVERAGE)));
                    student.setEmail(data.getString(JSON_STUDENT_EMAIL));
                    student.setLevel(Integer.parseInt(data.getString(JSON_STUDENT_LEVEL)));
                    student.setGroup(Integer.parseInt(data.getString(JSON_STUDENT_GROUP)));
                    student.setSection(Integer.parseInt(data.getString(JSON_STUDENT_SECTION)));
                    student.setConfirmed(data.getString(JSON_STUDENT_CONFIRMED).equals("1"));
                    response.setIsStudent(true);
                    response.setStudent(student);
                } else {
                    Professor professor = new Professor();
                    professor.setId(data.getString(JSON_PROFESSOR_ID));
                    professor.setFirstName(data.getString(JSON_PROFESSOR_FIRST_NAME));
                    professor.setLastName(data.getString(JSON_PROFESSOR_LAST_NAME));
                    professor.setEmail(data.getString(JSON_PROFESSOR_EMAIL));
                    professor.setDegree(data.getString(JSON_PROFESSOR_DEGREE));
                    response.setProfessor(professor);
                    response.setIsStudent(false);
                }
            }
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SignUpResponse getSignUpResponse(String jsonText) {
        // TODO: 11/03/2018 the code to handle the sign up response
        return null;
    }

    public static ArrayList<Subject> getSubjectList(String response) {
        return null;
    }

    public static ForgetPasswordResponse getForgetPasswordResponse(String response) {

        try {
            return new ForgetPasswordResponse(Integer.
                    parseInt(new JSONObject(response)
                            .getString(KEY_JSON_STATUS)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
