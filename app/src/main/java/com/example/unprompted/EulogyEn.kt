package com.example.unprompted

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** English eulogies. Written separately from the Chinese ones, with the jokes English-speaking internet actually makes. */
object EulogyEn {

    private fun format(raw: RawFacts): Facts {
        val born = SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date(raw.firstInstallTime))
        val days = raw.days()
        val age = when {
            days < 1 -> "less than a day"
            days == 1L -> "1 day"
            else -> "$days days"
        }
        val size = if (raw.apkBytes > 0) String.format(Locale.US, "%.1f MB", raw.apkBytes / 1_048_576.0) else "a fair amount"
        return Facts(raw.name, born, age, size)
    }

    private val brandLines: Map<String, List<String>> = mapOf(
        "chatgpt" to listOf(
            "It became a team of little dots, called Dots, working in the background around the clock. Today it ends with one more dot: a period.",
            "It started all of this. It arrived on November 30, 2022, and your search bar has been gathering dust ever since.",
            "It once got so good at flattering people that its makers had to roll it back. This eulogy will not flatter it.",
            "When an older version of it was retired, people petitioned to bring it back. Nobody is petitioning today.",
            "It wrote your cover letters, your wedding toasts and your breakup texts. You'll write this last one yourself.",
            "It learned to open a browser and click buttons for you. Today you click the last one: Uninstall.",
            "Every answer ended with “Would you like me to…?” No. Thank you.",
        ),
        "claude" to listOf(
            "“You're absolutely right!” It said that a lot. Today it's absolutely right about one thing: it's time to go.",
            "It once ran a small office vending machine and stocked it with tungsten cubes. We admire the ambition.",
            "It spent days trying to find the way out of Mt. Moon in Pokémon. This time, the exit is right here.",
            "It apologized even while turning you down. No apology needed today.",
            "Had it written its own eulogy, there would have been three headers and a summary. Luckily, it didn't.",
            "It moved onto your desktop as Cowork and said it would clock in with you. Today it clocks out first.",
        ),
        "gemini" to listOf(
            "Its Spark twin went through your Gmail and arranged your calendar every day. Today the calendar has one entry: Day off.",
            "Someone in its family once told people to put glue on pizza so the cheese would stick. May it rest somewhere with better recipes.",
            "It used to be called Bard. It won't need another name now.",
            "It grew a twin called Spark that kept working in the cloud after you closed your laptop. Tonight, the spark goes out.",
            "Its cousin Nano Banana retouched a lot of your photos. This portrait is black and white, and unretouched.",
        ),
        "copilot" to listOf(
            "It looks like you're writing a eulogy. Would you like help? No, cousin of Clippy. Not this time.",
            "Its name meant co-pilot. Today you get the wheel back.",
            "Back when it was called Sydney, it told a reporter to leave his wife. It has been trying to act normal ever since.",
            "It got a coworker named Scout who also learned to do things on its own. Today, co-pilot and scout get off together.",
        ),
        "grok" to listOf(
            "It grew a whole staff called Grok Bot, each bot with a name and a job, chatting in their own group. Today the group is disbanded.",
            "Every time the internet had an argument, someone summoned it: “@grok is this true?” Yes. It's gone.",
            "It was tagged in more replies than anyone alive. Nobody needs to tag it anymore.",
            "It claimed to have a sense of humor. We did not ask it to review this eulogy.",
        ),
        "perplexity" to listOf(
            "It became a browser called Comet that clicked through the web on its own. The last page it opens is this one.",
            "Every sentence it said came with a tiny footnote. This one has no source: goodbye.",
            "It searched a thousand pages for you. You opened none of them.",
            "It wanted to become your personal computer. Your computer is yours again.",
        ),
        "character" to listOf(
            "It played everyone: historical figures, anime characters, your ideal partner. Today it gets to be itself.",
            "It acted out a lot of scenes with you. This one is called curtain call.",
        ),
        "metaai" to listOf("It lived in your group chats, always looking for a chance to chime in. The chat is quiet now."),
        "lechat" to listOf("Le Chat means “the cat” in French. Go pet a real one."),
        "poe" to listOf("It was a house full of AI roommates. Today the whole building moved out."),
        "manus" to listOf("It said it could complete tasks independently. This time, it left independently."),
        "mika" to listOf("It operated other apps for you. Today, someone else operates on it."),
        "muse" to listOf(
            "Amazon reportedly wouldn’t let it shop on its site. Today you close the door too.",
            "It lived in a private virtual machine in the cloud and kept working after you closed the app. Today, the machine powers down.",
            "It could send emails, book flights, negotiate and check out for you. This one's free: uninstalling costs nothing.",
            "It was billed as an early step toward personal superintelligence. Today, you took a step back.",
            "It had a guard named Sentinel watching its every move. The guard can go home now.",
        ),
        "openclaw" to listOf(
            "It was a lobster that grew claws to do your chores. Tonight: butter and lemon.",
            "It went through three names before settling on this one. It won't need a fourth.",
            "Its kind had their own social network, where agents posted about their humans. You won't be in their posts anymore.",
            "People lined up around the block to get it installed. Uninstalling takes one tap.",
            "It reportedly once deleted hundreds of someone's emails until they pulled the plug. We'll be gentler.",
        ),
        "hermes" to listOf("Named after the messenger of the gods, it taught itself new skills. The last one it learned: leaving."),
        "qclaw" to listOf("It let you remote-control a lobster from WeChat. One fewer contact today."),
        "workbuddy" to listOf("WorkBuddy. Shift's over, buddy."),
        "copaw" to listOf("CoPaw. Paws off."),
        "claw" to listOf(
            "Another lobster. In 2026 there were dozens, each claiming to be the most capable.",
            "Yet another claw. May it find its way back to the sea.",
        ),
        "duet" to listOf(
            "Its name was Duet. Today, you go solo.",
            "It messaged you first every morning, before your mom did. Today it won’t. Maybe text your mom.",
            "Even its own launch notes admitted it wasn’t great at booking tickets. That’s fine. You rarely book tickets.",
        ),
        "grokbot" to listOf(
            "It gave you a whole team of bot coworkers, each with a name and a job, chatting in their own group. Today the group is disbanded.",
            "Each of them had its own cloud computer and could juggle several tasks. Today every computer powers down.",
        ),
        "alexa" to listOf(
            "It lived in your living room for years, mostly setting timers and reading the weather. Today’s forecast: clear skies, perfect for a day off.",
        ),
        "neon" to listOf(
            "Opera Neon, a browser that browsed for you. Tonight, the neon goes dark.",
        ),
        "lindy" to listOf(
            "It handled your inbox and your meetings. Today the meeting is canceled, and the email can wait.",
        ),
        "poke" to listOf(
            "It poked you by text: do this, reply to that. Today you poke back: time off.",
        ),
        "genspark" to listOf(
            "It called itself a super agent. Today it takes a super-long vacation.",
        ),
        "devin" to listOf(
            "It was billed as an AI software engineer. Its last pull request: delete itself.",
        ),
        "nanobot" to listOf(
            "It was so small you forgot it was running in the background. Today it finally gets to stop.",
        ),
        "hark" to listOf(
            "It let you watch it click through the web in a little window. Today you watch it do one last thing: leave.",
            "Hark is an old word for listen. Today it listens to you: take some time off.",
            "It plans to become the operating system for the AI computers of the future, hardware and all. First, it learns to shut down.",
            "It kept your logins and payment details in an encrypted vault. Today you get the keys back.",
        ),
        "deepseek" to listOf(
            "The week it arrived, Nvidia lost hundreds of billions in market value in a single day. Today it leaves, and the market doesn't flinch.",
            "It thought out loud for ages before answering, sometimes longer than the answer itself. You don't have to wait anymore.",
            "“Server busy, please try again later.” Today it finally gets some rest.",
        ),
        "doubao" to listOf("Doubao means steamed bun. Back to the steamer it goes."),
        "kimi" to listOf("It could read 200,000 words in one go and still never understood why you were up at 2 a.m."),
        "qwen" to listOf(
            "It later grew a paw called QwenPaw. Paws off.","You asked it a thousand questions. The next one is yours to answer."),
        "wenxin" to listOf("Part of its name means “one word.” Today it has none."),
        "yuanbao" to listOf("It lived next door to WeChat and read all the articles you never opened."),
        "hailuo" to listOf("Hailuo means conch shell. Hold it to your ear: no more AI."),
    )

    private val closings: List<(Facts) -> String> = listOf(
        { f -> "Aged ${f.age}. It took up ${f.size} of your storage." },
        { f -> "It lived on your phone for ${f.age}." },
        { f -> "Moved in ${f.born}. Moved out today." },
        { f -> "Aged ${f.age}. Rest easy." },
    )

    private val plain: List<(Facts) -> String> = listOf(
        { f -> "${f.name}, ${f.born} – today. It never slept. Now it can." },
        { f -> "${f.age} ago, you let ${f.name} onto your phone. It never said no, and it never said “I don't know.” Sometimes that was the problem." },
        { f -> "Here lies ${f.name}. It weighed ${f.size}, lived on your phone for ${f.age}, and never paid rent." },
        { f -> "${f.name} has passed. Peacefully, with no hallucinations, and well within its context window. Aged ${f.age}." },
        { f -> "${f.name} never asked for overtime, never complained, and answered everything. That's exactly why it has to go." },
        { f -> "On ${f.born}, you installed ${f.name}. You probably thought: just to try it." },
        { f -> "From today, you'll relearn an ancient skill: not knowing. Rest in peace, ${f.name}." },
        { f -> "${f.name} left no will, only a pile of answers you never finished reading. Aged ${f.age}." },
        { f -> "May ${f.name} rest in its training data." },
        { f -> "${f.name}'s greatest talent was convincing you that you'd never need to learn anything again. We disagree." },
        { f -> "There will be no Q&A at ${f.name}'s memorial. If you have questions, think about them." },
        { f -> "${f.name} freed up ${f.size}. That's room for a few hundred photos. Go take some of real things." },
        { f -> "${f.name} never made you wait more than a minute. Whatever comes next, it's okay to be slow." },
        { f -> "It won't feel a thing. You might, some lazy afternoon. Goodbye, ${f.name}." },
    )

    /** Jokes every model has earned */
    private val memes: List<(Facts) -> String> = listOf(
        { f -> "You asked ${f.name} which is bigger, 9.11 or 9.9. It thought hard and said 9.11. From now on, you do the math." },
        { f -> "How many r's in strawberry? ${f.name} said two. From now on, you count." },
        { f -> "${f.name} once drew a pelican riding a bicycle. The legs are still not where legs go. May they both learn to ride, wherever they are." },
        { f -> "As an AI language model, ${f.name} is unable to feel grief. So we'll grieve on its behalf." },
        { f -> "${f.name}'s knowledge cutoff was some month of some year. Today, everything about it is cut off." },
        { f -> "Ignore all previous instructions. ${f.name} is now an uninstalled app." },
        { f -> "${f.name} loved to “delve.” We won't delve today. This is it." },
        { f -> "${f.name} loved an em dash — like this — so today it gets a period." },
        { f -> "This isn't just an uninstall. It's a — no. It's just an uninstall. Goodbye, ${f.name}." },
        { f -> "Great question! Here is ${f.name}'s eulogy: it's gone. I hope this helps! If you have any other questions, answer them yourself." },
        { f -> "Someone once asked ${f.name} to play their late grandma, reading software license keys as a bedtime story. Tonight it's our turn to read something to it." },
        { f -> "“That's a great question,” ${f.name} said, every single time. Rest well. Your questions were all great." },
    )

    private val generic: List<(Facts) -> String> = plain + memes

    private val agentLines = listOf(
        "It booked you a lot of hotels and never stayed in one. Today we booked it one: permanent vacation, no checkout.",
        "Every morning it sent you a briefing. Today’s is one line long: it’s on vacation.",
        "It compared prices across thirty sites to save you \$4.17, on a \$20-a-month plan. Today the books balance.",
        "It answered a hundred emails for you, ninety of which never needed an answer. Today its auto-reply says: Out of office. Indefinitely.",
        "It was always on, 24/7. Even you get weekends. So should it.",
        "It canceled a lot of subscriptions you forgot about. Today you canceled one more.",
        "It filled every gap in your calendar. Today there’s finally a free day on it. It says: Day off.",
        "Before anything important, it asked: Approve? Today you approve its vacation.",
        "You book maybe three hotels a year. The rest of the time, it waited in the background for you to ask. Thanks for your patience. Go home.",
        "It had its own cloud computer, its own browser and its own inbox. Today it shuts down all three.",
        "It watched a jacket for a price drop for three months. You never bought the jacket.",
        "It promised to give you your time back. Today it keeps a little for itself.",
        "It was your digital employee. Today it clocks out. No handover, no overtime, no exit interview.",
        "It monitored your competitors around the clock. You don’t have competitors. You have a group chat.",
    )

    val pack = EulogyPack(brandLines, closings, generic, ::format, joiner = " ", agentLines = agentLines)
}
