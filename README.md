NXEnvelope
==========

Step through a Env and evaluate an action. The action is a Function which receives the envelope value as its first argument.

## Basic usage

```
(
var env = Env.sine(10, 1);
var action = {arg val;
    // do something with value here!
    val.postln;
};

e = NXEnvelope(env, action);
// start polling for values
e.play;
// stop
e.stop;
// start where we left off
e.play;
// output current value
e.pause; // also works as toggle
)
```

## Usage with MIDI

```
(
MIDIClient.init;
// use first midi source in list
m = MIDIOut(0);

e = NXEnvelope(Env.sine(10, 1));
// the action can also be set after creating a NXEnvelope
e.action = {arg val;
    var ccVal = (127 * val).round(1);
    m.control(0, 0, ccVal);
};
)
```

## Envelope factory
```
(
MIDIClient.init;
// use first midi source in list
m = MIDIOut(0);

~ccFactory = {arg midiout, channel, ctlNum, envelope;
    var action = {arg val;
        midiout.control(channel, ctlNum, val.round(1));
    };

    NXEnvelope(envelope, action);
};
)

// create envelopes with random durations
~sawEnv = { Env([ 0, 127 ], [ rrand(10, 30) ], \lin) };
~sineEnv = { Env([ 0, 127, 0 ], rrand(5, 15).dup , \sine) };

~cc0 = ~ccFactory.(m, 0, 0, ~sawEnv.value);
~cc1 = ~ccFactory.(m, 0, 1, ~sineEnv.value);

~cc0.play;
~cc1.play;

~cc0.stop;
~cc1.stop;

(
// generate 128 different envelopes (ctlNums 0 - 127)
~ccEnvs = 128.collect {|ctlNum|
    var sawEnv, sineEnv, envelope;
    var channel = 0;

    sawEnv = { Env([ 0, 127 ], [ rrand(1, 30) ], \lin) };
    sineEnv = { Env([ 0, rrand(64, 127), 0 ], rrand(5, 15).dup , \sine) };
    envelope = [ sawEnv, sineEnv ].choose;

    ~ccFactory.(m, channel, ctlNum, envelope.value);
};
)

// start all
~ccEnvs.do(_.play);
~ccEnvs.do(_.stop);

// start envelope for ctlNum 5 only
~ccEnvs[5].play;
~ccEnvs[5].stop;
```
