function setTitle(text) {
    var fieldNameElement = document.getElementById("title");
    if (fieldNameElement.firstChild)
        fieldNameElement.firstChild.nodeValue = text;
}

function setSubtitle(text) {
    var fieldNameElement = document.getElementById("subtitle");
    if (fieldNameElement.firstChild)
        fieldNameElement.firstChild.nodeValue = text;
}

function getDay() {
    var d = new Date();
    var weekday = new Array(7);
    weekday[0] = "Sunday";
    weekday[1] = "Monday";
    weekday[2] = "Tuesday";
    weekday[3] = "Wednesday";
    weekday[4] = "Thursday";
    weekday[5] = "Friday";
    weekday[6] = "Saturday";

    return weekday[d.getDay()];
}

function getSubtitle() {
    var notSunday = [];
    var Sunday = [];

    notSunday[0] = "But go to church anyway!";
    notSunday[1] = "Why are you not in church though!?";
    notSunday[2] = "remember that Jesus is watching you";
    notSunday[3] = "Jesus would have wanted you to be in church!";
    notSunday[4] = "What time is it? Church Time!";
    notSunday[5] = "if you go to church now, you might not go to hell!";

    Sunday[0] = "Time to go to church!";

    var day = new Date().getDay();
    if (day == 0) {
        var random = Math.floor((Math.random() * Sunday.length));
        return Sunday[random];
    } else {
        var random = Math.floor((Math.random() * notSunday.length));
        return notSunday[random];
    }
}

var day = getDay();
if (day == "Sunday") {
    setTitle("Yes, today it is " + day + "!");
    setSubtitle(getSubtitle());
}
else {
    setTitle("No, today is " + day + ".");
    setSubtitle(getSubtitle());
}