# Coroutine Example

> When countdown is reach to 0, The christmas tree will be invisible.

### What you can do with this app.
- Show/Hide button to show the christmas tree (animated with Lottie)
- Countdown 10 seconds on UIThread (Sync)
- Countdown 10 seconds on BackgroundThread by callint viewModelScope.launch(Dispatchers.IO)
- Countdown 10 seconds on UIThread that will call countdown function on BackgroundThread (Suspend)
