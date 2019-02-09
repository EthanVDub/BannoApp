<h1>Banno Boot Camp Application</h1>
This project has been done as an application for Banno.

To run these files, either download the class file and run it in your own IDE (you will need jsoup imported in order to do that), or simply
open your command prompt and type "java -jar BannoApp.jar" while in the working directory of the file BannoApp.jar. It will
print the results to the console.

There are two versions of the base file. BannoHTMLParser.java has a main method that runs when you run the file. This is the 
version used for the jar file and is what you would normally run. The other version, BannoScraper.java turns all the code from the main
method into methods themselves. This would be useful for having another class that called this class for data.

<h2>How I went about solving this problem</h2>
Last year, I used python's beautiful soup to gather the data and print it to the command line. Since then, I have learned
a lot about programming, data structures, efficiency, etc. I wanted to make sure this year I tried to do things more efficiently than
last year. 

When I started out, I was hoping to do the project in javascript using request-promise for the data fetching and React
for the formatting (you can see a project of me getting data from a RESTful API and using it in a react project in my github). 
However, when I tried to do this I found out that banno.com has a no-cors policy in its header, which made it so I couldn't 
gather data using JS. This is the first time I have dealt with this issue, so if there is a workaround, I do not know it.
I did some research and it looked like there wasn't going to be a way to use javascript to request the data.

From there, I decided to use java as I knew it had a library similar to beautiful soup and I have had a lot of experience in java. 
Using jsoup, I could very quickly gather all of the data that I needed. I also used the chrome extension "SelectorGadget" 
to find the data paths.

In my java file, I have thoroughly described every step of gathering the data, so look there for more information.
If you have any questions on the project, let me know.
