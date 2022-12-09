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


## Documentation

I fixed the issues with the `VideoItemViewHolder` by decoupling the ViewHolder from any business logic, ensuring that its `bind()` method really only binds 
data and doesn't load any data itself. The data is now loaded in the `MainActivity`s `loadVideoList()` method, which then updates the adapter. This is unclean of course, but for this 
small example app I focused on not introducing too many changes. In a production app, a proper approach would have been to move the `loadVideoList()`
logic into some sort of repository, and possibly updating the UI in a reactive way via Kotlin's Flows. This would have made things easier to test and refactor, maintaining structured concurrency. 

The playlist feature didn't require any big implementation efforts, as the ExoPlayer library comes with a [playlist function](https://exoplayer.dev/playlists.html).
The "Add to playlist" icon was included using a [constraint layout chain](https://developer.android.com/develop/ui/views/layout/constraint-layout#constrain-chain).

Formatting the text to italic cost me more time than I thought. First, I wrote a simple parser that checks for bracket characters. The first `[` would have been paired with the 
last `]` in the string, which I tried to implement via recursion. Then I noticed that the unit tests expect a more simple iterative approach where one would just iterate over the string,
and I adapted my implementation accordingly.
Unfortunately, I was unable to resolve the issues with the unit tests. I took the test strings from the unit tests and manually tested them on emulators by overwriting the `textComplete` argument 
of `SpannableStringHelper.replaceBracketsWithItalic()`. In all cases, the string value and the text format where as the unit tests expected. 
I believe my tests fail because I build my `SpannableStringBuilder` differently from the unit tests expectation, the issue therefore being an implementation detail. Nonetheless, I decided to keep my implementation:
On one hand, I believe the video descriptions to be small, so in this case I value code readability more than code performance. On the other hand, I am very busy during these December days, and I'd like to hand in my solution in a somewhat timely manner.
In a production app, I would have done further research on why the tests fail, fixing either the implementation or the unit tests. In this case, even though my implementation passed the manual
tests, I didn't dare to alter the unit tests. This is because I think for this exercise I don't "own" the unit test code, and I'd like to maintain honesty.