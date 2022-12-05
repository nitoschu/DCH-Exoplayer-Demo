Android Coding Challenge
========================

The project contains a basic video player app that unfortunately has a couple of issues. Please fix the following bugs:

- The video grid sometimes shows the 4K-icon for videos that are not marked as HD content in the data.
- The video grid temporarily displays the wrong thumbnails for videos when scrolling.
- The video grid sometimes displays the wrong thumbnail for videos.

Please also extend the app in the following ways:

1. Implement a simple playlist feature:
   - Display both "play" and "playlist add" icons on the video thumbnails.
   - Clicking the play icon on a video should clear the playlist and start playing the video.
   - Clicking the playlist icon should add the video at the end of the playlist and it should start playing automatically after the previous item(s).
   - Skipping forward/backward within the playlist should be possible with the default player controls.
2. The video descriptions contain "[","]" markers that should be replaced with italic text formatting. 
   - There are unit tests for the expected outcome in `SpannableStringHelperTest.kt`, please enable them by removing `@Ignore`.

Please push your solution to Github or a similar SCM hosting service. Also document briefly how you fixed the issues and implemented the enhancements.
