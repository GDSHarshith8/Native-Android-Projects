# 🎵 Music Streaming Player  

> 🎧 A sleek Android music streaming player built with Jetpack Compose and Media3 ExoPlayer.  

### Your Music Stream, Reimagined. 
Meet **Music Streaming Player**, a modern Android music player built on a **search-first MVVM architecture** for fast track discovery and clean state management, delivering a seamless streaming experience.

We believe listening should be effortless. Music Streaming Player puts you in control with robust playback controls and intelligent offline handling, ensuring your music never skips a beat.  

---

### **Core Music & Playback Features**  

Music Streaming Player is engineered for a high-quality, uninterrupted listening experience.  

* **API-Powered Library:** Fetches a dynamic list of free audio tracks from a public music source — **Jamendo**.  
* **Detailed Track Display:** Shows Title, Artist, Duration, and Thumbnail for each track.  
* **Graceful Error Handling:** Smoothly manages loading states and displays clear messages for network or API issues.  
* **Intelligent Sorting:** Quickly find tracks with built-in sorting options, easily toggled via chips.  
  - Sort by Name (A–Z).  
  - Sort by Duration (Shortest to Longest).  
  - Sort by Closest Match (Search relevance).  
* **Robust Audio Playback:** Streams audio using the powerful **Media3 ExoPlayer** with full playback controls—Play / Pause, position tracking, and proper lifecycle management.  
* **Media3 Notifications:** Control playback directly from system notifications with **Play, Pause, Skip/Forward, Previous and Next** actions, keeping your music accessible even outside the app.

---

### **Enhanced Listening & Stability**  

Music Streaming Player is built for a smooth, uninterrupted listening experience that feels effortless from the first tap.  

* **Advanced Playback Controls:**  
  Enjoy complete control over your music with intuitive playback options.  
  - Effortlessly **scrub through audio** with a responsive seek bar.  
  - Switch tracks instantly using **Next** and **Previous** controls.  
  - Keep your music playing in the background thanks to a dedicated **Android Service** that powers continuous playback even when the app is closed.  

* **Networking Excellence:**  
  Stream your favorite tracks quickly and reliably, no matter the connection.  
  - Powered by **Ktor** for fast and efficient network performance.  
  - Maintains stability with **graceful handling** of all network interruptions or API errors.  
  - Ensures smooth data fetching and parsing with **Kotlinx Serialization** for optimal performance.  

* **Polish & Experience:**  
  Every detail is crafted to make your listening experience visually pleasing and intuitive.  
  - Immerse yourself in a **clean, Material 3 UI** that looks great in both light and dark themes.  
  - Enjoy **Dark Mode support** that adapts seamlessly to your system settings.  
  - See your favorite tracks come alive with **album art and thumbnails** loaded smoothly using **Coil**.  
  - Experience smooth animations, clear layouts, and a consistent design that makes every interaction delightful.  

---

### **Under the Hood**  

Music Streaming Player is built on a modern, robust, and scalable Android foundation, leveraging the latest Jetpack libraries.  

- **Jetpack Compose:** The backbone of a modern, declarative, and highly performant UI.  
- **Media3 ExoPlayer:** Provides reliable, feature-rich streaming and playback capabilities.  
- **Hilt:** Simplifies dependency injection and promotes a clean architecture.  
- **Ktor:** Powers efficient and robust HTTP requests.  
- **Kotlinx Serialization:** Ensures safe and efficient JSON parsing.  
- **Coil:** Handles asynchronous image loading and caching for album art and thumbnails.  
- **DataStore:** Provides modern and reliable local data persistence for cached responses and settings.  
- **API Reference:**  
  Powered by the Jamendo public API  
  🔗 [https://api.jamendo.com/v3.0/](https://api.jamendo.com/v3.0/)

---

### 🚀 Future Enhancements  
- Playlist creation and management  
- Track favorites and personalized recommendations  
- Enhanced offline playback support  
