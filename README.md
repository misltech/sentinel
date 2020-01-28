# sentinel

This will continuously watch for an open spot on new paltz schedule for classes website. When a seat is found it will send you a text message. This program expires after 7 days of runtime.  This program was designed to be used during syllabus week while everyone is still switching classes.

# prerequisite

Some commandline experience needed.
You will need to have Java installed. 
This program was created to run on a linux server that the school provides. By default Java is installed!

# how to run

Run ***`nohup command &`*** in the commandline
***nohup*** allows you to close the terminal and still have the program running. ***&*** allows the program to run in the background.

    nohup java -jar sentinel.jar {args} &

***args are required***. Can also be requested when `java -jar sentinel.jar` is typed without arguments in the commandline

    Semester, Year, Class_Subject, Course_CRN, Check_Frequency, Phone_Number

**Semester {String}** ex: Spring, Summer, Winter\
**Year {int}** please check that the schedule exist before requesting it.\
**Check Frequency_inMinutes** {int} has to be greater than 5 mins. Input represents a minute.\    
**Class_Subject** {String} If the subject has a space or other delimiters put them together. It is also not case sensitive.\
**Course_CRN{int}** You can find this on the website.\
**Phone_Number {int}** U.S numbers only.\
Ex: `nohup java -jar sentinel.jar Spring 2020 Geology 149 5 5555555555 &`\
Ex2: `nohup java -jar sentinel.jar Spring 2020 ComputerScience 293 5 5555555555 &`

After running this program on linux it will give you a PID code. Write it down somewhere as thats how you'll end the program.

# Check runtime

To check if the program is running. Check the last 5 lines of nohup.out using this in the same directory.
```tail -f nohup.out```

# How to end program

     kill -9 PID

#  license

Free to use and modify at your own risk ;)

made with stackedit.io
