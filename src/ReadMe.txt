C195 Software II(Java) Project by Jack Jutzi
-------------------------------

Requirments and functionality:

1A. The opening screen is the login form. Username is: test.  Password is: test.
1B. The user's language is found automatically on line 27 of src/View_Controller/LoginScreenController
1C. This data is used on lines 51 - 71 to translate all fields into either English or French.
1D. The first error control on lines 73 - 79 throws if either the username of password field is empty.
1E. The second error control on lines 113 - 118 throws if the login is invalid (username or password is incorrect).

2A. After logging in, the main screen contains the Customer data in the left table.
    Selecting a customer and clicking delete will remove customer from the database.
2B. Clicking Add of Modify will take you to the add/modify screen where you can either
    Add a new customer or modify an existing one (including the required name, address, and phon number fields).
2C. Clicking save will (assuming all fields have values) update the customer in the database and
    return you to the main screen.

3A. On the main screen, the table on the right contains all appointments data from the current day forward.
3B. The functionality to add/modify/delete appointments is the same as customers, although the UI uses combo
    boxes instead of text inputs.
3C. Each appointment includes link to customerId and customerName through the SQL query on line 126 of
    src/View_Controller/MainScreenController

4A. On the main screen, there are three radio buttons to filter appointments.
    All is self-explanatory.
    Month will filter appointments to only show those in the next 30 days.
    Week will filter appointments to only show those in the next 7 days.

    **By default, appointments prior to the current day (in local date) are not shown.

5A. All appointment times retrieved from the database are automatically converted using Java classes into the
    user's local date and time.  In src/View_Controller/MainScreenController this logic is on lines 147 - 177 (Main screen).
5B. All appointment times are converted from local date and time to database timestamps when appointment is saved.
    In src/View_Controller/AddModifyApptController this logic is on 208 - 221.

6A. Scheduling hours are controlled by default to only range from 6am to 5pm in the user's local time, preventing
    appointments from being scheduled outside these business hours.
6B. Saving throws an error if the user attempts to schedule an appt that overlaps with an existing appointment
    (same consultant). This logic is in src/View_Controller/AddModifyApptController on lines 296 - 325
    And this function is called on line 224.
6C. In the add/modify customer screen, an error is thrown if the user attempts to save a customer record with an
    empty or invalid field.
    This logic is in src/View_Controller/AddModifyCustomerController on lines 117 - 133.
    This function is called on line 100.
6D. The fourth exception control is referenced above in 1E.

7A. Both required lamdas are included in the src/View_Controller/MainController file.
    The first is on line 195, and it filters out appts prior to today (local time).
    The second is on line 211, and it filters for appts within the next 15 min.

8A. Once the user logs in and is sent to the main screen, the logic in src/View_Controller/MainController file
    throws an alert if there is an appointment within 15 minutes of the user's login.
    This logic is on lines 210 - 226

9A. On the main screen, click the reports button below the appointments table.
    This takes you to the reports screen where three reports are generated and displayed.
9B. The first shows all appointments for each customer.
9C. The second (to the right) shows each consultant and their respective schedules by listing all their
    appointments in order.
9D. The third (to the bottom) shows the number of unique appointment types by month.

    ** For the third, there are 4 possible appointment types.  But if there are, for example,
    4 appointments in January, and all of them are the same type, January only has 1 unique type of appointment counted.

10A. Upon login, the user's username and local date/time are logged to the log.txt file.
     If that file doesn't exist, the FileWriter will first create it, then log the time.
     This logic is in src/View_Controller/LoginScreenController on lines 91 -102.



