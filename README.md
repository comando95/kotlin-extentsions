# Kotlin Extensions
Kotlin Extensions is a library of Extensions I use a lot. They help me quickly call up commands I often use.
# Begin

To add kotlin extensions to your project (Using Android Studio and Gradle):

 add implementation 'com.github.comando95:kotlin-extensions:1.0.0 to your build.gradle dependency block.

 For example:

 ```
 repository {
 maven { url = uri("https://jitpack.io") } or maven { url 'https://jitpack.io' }
 }
 dependence {
 deploy 'com.github.comando95:kotlin-extensions:1.0.0'
 }
 ```

Example of hiding and showing views:
 ```
 // To hide a view
 view.gone()

 //To show 1 view
 view.visible()
 ```
 instead you have to call:
 ```
 view.visibility = View.GONE or View.VISIBLE
 ```

 Example of hiding/showing the keyboard:
 ```
 view.showSoftKeyboard()
 view.hideKeyboard()

 ```
 instead you have to call:
 ```
 showSoftInput or hideSoftInputFromWindow
 ```
 Example to get screen/app ratio:
 ```
 //At activity
 getScreenWidth() //Get the original screen width
 getScreenHeight() //Get the original screen height

 getAppScreenWidth //Get the app's screen width
 getAppScreenHeight //Get the app's screen height

 //At Fragment or Context
 activity?.getScreenWidth()
 ...
 ```

There are many other things, you can learn them yourself and I am very pleased to receive your comments.