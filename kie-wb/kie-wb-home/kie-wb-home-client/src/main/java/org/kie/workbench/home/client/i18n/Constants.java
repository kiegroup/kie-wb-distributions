/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.home.client.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * This uses GWT to provide client side compile time resolving of locales. See:
 * http://code.google.com/docreader/#p=google-web-toolkit-doc-1-5&s=google-web-
 * toolkit-doc-1-5&t=DevGuideInternationalization (for more information).
 * <p/>
 * Each method name matches up with a key in Constants.properties (the
 * properties file can still be used on the server). To use this, use
 * <code>GWT.create(Constants.class)</code>.
 */
public interface Constants
        extends
        Messages {

    Constants INSTANCE = GWT.create(Constants.class);

    String SignOut();

    String WelcomeUser();

    String There_is_no_variable_information_to_show();

    String Variable();

    String Value();

    String Last_Time_Changed();

    String View_History ();

    String Variable_History_Perspective ();

    String Show_me_my_pending_Tasks ();

    String I_want_to_start_a_new_Process ();

    String I_want_to_design_a_new_Process_Model ();

    String I_want_to_design_a_new_Form();

    String I_want_to_create_a_Task();

    String Show_me_all_the_pending_tasks_in_my_Group();

    String Show_me_my_Inbox();

    String Hooray_you_don_t_have_any_pending_Task__ ();

    String Id();

    String Task();

    String Status();

    String Due_On();

    String Details();

    String Request_Details_Perspective_Errai();

    String No_KBases_Available();

    String Please_Select_at_least_one_Task_to_Execute_a_Quick_Action();

    String Priority();


    String No_Parent();

    String Parent();

    String Edit();

    String Task_Edit_Perspective();

    String Work ();

    String Form_Perspective ();

    String No_Process_Definitions_Available ();

    String Name ();

    String Package ();

    String Type ();

    String Version ();

    String Start_Process ();

    String Actions ();

    String Process_Definition_Details ();

    String No_Process_Instances_Available ();

    String Aborting_Process_Instance ();

    String Signaling_Process_Instance ();

    String Process_Id ();

    String Process_Name ();
    
    String Process_Version ();

    String State ();
    
    String Process_Instance_Start_Time();

    String Process_Instance_Details();

    String Hooray_you_don_t_have_any_Group_Task_to_Claim__();

    String Task_Id ();

    String Task_Name ();

    String Actual_Owner ();

    String Description ();

    String Completed ();

    String Pending ();

    String Personal_Task_Statistics ();

    String Aborting_Process_Instance_Not_Allowed();

    String Signaling_Process_Instance_Not_Allowed();

    String Old_Value();
}