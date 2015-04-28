Ginger4j
==========

Java wrapper for correcting spelling and grammar mistakes based on the context of complete sentences.

## Requirements

Java 7 or higher.

## Usage

```java
String text = "The smelt of fliwers bring back memories.";
Ginger4J ginger = new Ginger4J();
JSONObject result = ginger.parse(text);

// Pretty print the result
System.out.println(result.toString(4));

// Get the correct phrase
String correctPhrase = ginger.getResult();
System.out.println(correctPhrase);
```

```
// Output:
// Result
{
    "result": "The smell of flowers brings back memories.",
    "text": "The smelt of fliwers bring back memories.",
    "corrections": [
        {
            "text": "smelt",
            "correct": "smell",
            "definition": "",
            "start": 4,
            "length": 5
        },
        {
            "text": "fliwers",
            "correct": "flowers",
            "definition": "a plant cultivated for its blooms or blossoms",
            "start": 13,
            "length": 7
        },
        {
            "text": "bring",
            "correct": "brings",
            "definition": "",
            "start": 21,
            "length": 5
        }
    ]
}
// Correct phrase
The smell of flowers brings back memories.
```

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

## Thanks

Thank you for [Ginger Software](http://www.gingersoftware.com/) for such awesome service. Hope they will keep it free :)

Thanks to @subosito for this inspriration https://github.com/subosito/gingerice (Ruby Gem).
