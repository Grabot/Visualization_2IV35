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

function setBibleQuote(text) {
    var fieldNameElement = document.getElementById("Bible");
    if (fieldNameElement.firstChild)
        fieldNameElement.innerHTML = text;
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
    notSunday[6] = "Jesus did not needed internet to tell him which day it is!";

    Sunday[0] = "Time to go to church!";
    Sunday[1] = "What the hell are you doing on internet go to Church!";
    Sunday[2] = "It's almost monday, last chance to go to church!";

    var day = new Date().getDay();
    if (day == 0) {
        var random = Math.floor((Math.random() * Sunday.length));
        return Sunday[random];
    } else {
        var random = Math.floor((Math.random() * notSunday.length));
        return notSunday[random];
    }
}

function getBibleQuote() {
    var BibleQuotes = [];

    BibleQuotes[0] = "Numbers 31:32<br>These were the spoils which remained of the plunder taken by the fighting men: 675,000 sheep, 72,000 cattle, 61,000 donkeys, and as for persons, 32,000 young women who had had no intercourse with a man.";
    BibleQuotes[1] = "Genesis 15:9<br>The Lord answered, \"Bring me a heifer three years old, a she-goat, three years old, a ram three years old, a turtle dove and a young pigeon.\"";
    BibleQuotes[2] = "Ezekiel 23:20<br>She lusted after her lovers, whose genitals were like those of donkeys and whose emission was like that of horses.";
    BibleQuotes[3] = "Leviticus 19:19<br>Do not wear clothing woven of two kinds of material.";
    BibleQuotes[4] = "Deuteronomy 23:1<br>No one whose testicles are crushed or whose penis is cut off shall be admitted to the assembly of the LORD.";
    BibleQuotes[5] = "Genesis 19:8<br>\"Look, I have two daughters, virgins both of them. Let me bring them out to you and you could do what you like with them. But do nothing to these men because they have come under the shelter of my roof.\"";
    BibleQuotes[6] = "Deut. 25:11<br>When two men are fighting and the wife of one of them intervenes to drag her husband clear of his opponent, if she puts out her hand and catches hold of the man by his privates, you must cut off her hand and show her no mercy.";
    BibleQuotes[7] = "Deut. 28:53<br>Then because of the dire straits to which you will be reduced when your enemy besieges you, you will eat your own children, the flesh of your sons and daughters whom the Lord has given you.";
    BibleQuotes[8] = "Judges 3:21<br>And Ehud reached with his left hand, took the sword from his right thigh, and thrust it into his belly. And the hilt also went in after the blade, and the fat closed over the blade, for he did not pull the sword out of his belly; and the dung came out.";
    BibleQuotes[9] = "Ezekiel 16:17<br>You also took the fine jewelry I gave you, the jewelry made of my gold and silver, and you made for yourself male idols and engaged in prostitution with them.";
    BibleQuotes[10] = "Leviticus 24:16<br>Whoever utters the name of the Lord must be put to death. The whole community must stone him, whether alien or native. If he utters the name, he must be put to death.";
    BibleQuotes[11] = "Psalm 137:9<br>How blessed will be the one who seizes and dashes your little ones Against the rock.";
    BibleQuotes[12] = "Timothy 2:12<br>I do not permit a woman to teach or to assume authority over a man. She must be quiet.";
    BibleQuotes[13] = "Jeremiah 19:9<br>I will make them eat the flesh of their sons and the flesh of their daughters, and they will eat one another's flesh in the siege and in the distress with which their enemies and those who seek their life will distress them.";
    BibleQuotes[14] = "Peter 2:18-19<br>Slaves, in reverent fear of God submit yourselves to your masters, not only to those who are good and considerate, but also to those who are harsh. For it is commendable if someone bears up under the pain of unjust suffering because they are conscious of God";
    BibleQuotes[15] = "Mark 14:51-52<br>Young man was following Him, wearing nothing but a linen sheet over his naked body; and they seized him. But he pulled free of the linen sheet and escaped naked.";
    BibleQuotes[16] = "Kings 2:23-24<br>And he went up from thence unto Bethel: and as he was going up by the way, there came forth little children out of the city, and mocked him, and said unto him, Go up, thou bald head; go up, thou bald head. And he turned back, and looked on them, and cursed them in the name of the Lord. And there came forth two she bears out of the wood, and tare forty and two children of them.";
    BibleQuotes[17] = "Leviticus 18:22<br>Do not have sexual relations with a man as one does with a woman; that is detestable.";

    var random = Math.floor((Math.random() * BibleQuotes.length));
    return BibleQuotes[random];
}

var day = getDay();
if (day == "Sunday") {
    setTitle("Yes, today it is " + day + "!");
}
else {
    setTitle("No, today is " + day + ".");
}
setSubtitle(getSubtitle());
setBibleQuote(getBibleQuote());