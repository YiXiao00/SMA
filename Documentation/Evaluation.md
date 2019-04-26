# Evaluation
In order to evaluate our project properly, we considered what methods would be most effective so we could tailor our methods for our project. The main aim from a consumer end is to allow users to manage their devices and tasks, and data (such as electricity consumption). This meant UI would be very important. We wanted to take feedback from people so we could create a user manual, making it so no one could get confused. We also wanted to evaluate how well the project fulfilled Alex's need, of being flexible and able to be adapted for a number of devices. Finally, we also wanted to evaluate how well the technical functions worked.

We decided due to these three needs, a variety of testing methods would be used. We would speak to a number of people, including our client to get some feedback on how they felt about our project. We would ask questions concerning user interface, ease of use, how bug-free their experience had been and their thoughts on it as a whole. Interviews are fantastic opportunities to get feedback live, as you can see how someone is engaging with the so fiware. UI problems , for example, are incredibly easy to notice when someone else is using the software. The form would also mean we could ask specific questions and follow up if there were ambiguous answers - as opposed to an online survey, which could lead to some "incomplete data".

We would also implement unit testing, showing that our project worked as intended with suitable error checking and minimal side effects. We do not expect everything to be perfect, so we would like to assess ourselves and understand which parts have been done well and which could be improved a lot. Unit testing is a fantastic way to ensure the project is reliable and useful. Whilst implementing it, we realised we should have made it a significant part of our project from the start. It was always our intention to develop testing alongside our project, and this is something that unfortunately fell to the wayside.

# Interview One

I spoke to my friend Eva, in an attempt to establish what she liked about the project and try and get some valuable feedback. I did not give her any instructions, only informing her it was a project for managing Smart Homes.

The signup and login were fine. She appreciated the choice to use cookies rather than have to constantly re-enter the username and password. She noted there was no way to delete her account from the UI. She did note the different styles between the sign up page and the actual dashboard, due to the fact they had been designed at different times. She preferred the style of the dashboard UI, saying it looked "professional". She was also a big fan of the animations, saying it gave the whole project feel easy to use and clever. She noted it was important that for Smart Devices the dashboard felt next generation. She did not there was a lot of wasted space, and didn't think it would work well on phones.

She found the program immediately confusing. We realised in a lot of testing, we'd designed around the idea that the user would already have devices visible - making it clear what to click. However, upon first signup the user would have no devices registered, and the user has to click around to understand how it works.

After adding a device, the UI "came together" in her words. It was easy to navigate around and add more devices. She liked how the form grew in a natural way, rather than seeing stretched out. She liked how versatile the design was, and that we'd chosen to use HTML and CSS to have the website change rather than a drop down form.

She found the two types of tasks very confusing, and we realised this was something that needs to be explained clearly in the user manual. I explained the difference between the two, and she found them easy enough to set up. She felt they were a little underwhelming, but as a test demonstrated the programs functionality well.

She concluded by saying the UI felt slick and well made, but the lack of actual connected features made it feel strange. However, she felt the program had a solid basis and if we were able to correctly connect it with devices it would work well. She could see the appeal in her home but ultimately said a mobile app would be the most effective way forwards: "I don't want to log on to my laptop every time I want to turn down my volume, especially when I'm carrying a smartphone with me."

#Interview One - Key points

The UI felt slick and professional, but also scalable. She liked how each device had their own page, and suggested this would make it easier to customise each device. She suggested each device could have a more stylised page. She said our choice to use CSS and animations really made it stand out. The need for a mobile app was addressed, and this is certainly something that would be a long term goal.

The user experience was confusing at first, showing the need for a user manual. Our needs are complicated by the fact our project is not just a product, but providing the code that could be changed and adapted for the future. Still, the need for a clear and concise user manual is clear.

One point of definite note is the confusion between two types of task, and how the different could be use. She suggested merging them into one type of task, and making a more flexible way of forming them. A task could be a condition and an outcome, both of which can be customised. This would make it very logical for people to follow.

The response to the project was positive, but the need for greater functionality was needed. If, hypothetically, we wanted to demonstrate or pitch it then it would not suffice in it's current state. Adding devices that could be interacted with, and seeing them change in real time would be a more impressive experience.


#Client Feedback

Not just at the end, but throughout the project we were fortunate to receive detailed and useful feedback from our client. Alex was able to highlight ways in which we could grow and develop the project, and luckily saved us from spending time on features that weren't important for the final goal. Due to his technical experience, we could get unique insights on how we should approach developing the project.

(more here)

#Unit Testing

There's a lot of technical elements to our project, which may potentially be used for real Smart Homes - potentially even for security devices. It is important that there are minimal side effects and every action the user can make is carefully constructed to ensure there is no way for a user to accidentally wipe their entire database.

The user is only able to interact with the database through a series of POST requests. These can return information and change the content of the databases. We carefully made a series of unit tests, which would perform these POST request and then performs tests to ensure these have been taken care of successfully.

These tests range from very simple (does signing up a user allow you to sign in at the user) to more complex edge cases. Implementing these allows us to feel confident in our functions. Users will use all functions available in whatever way they please, so these have to be watertight and not negatively impact one another.

We did not implement unit testing as we want along, and in hindsight that was a bad decision. Implementing every function with appropriate unit testing would have allowed us to build a project with more confidence. We ended up having some unfortunate side effects that were overlooked, but could have been caught if we had dealt with them properly.
