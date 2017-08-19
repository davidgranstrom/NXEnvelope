NXEnvelope {
    var <>env, <>action, <>resetOnStop;
    var <>updateFreq, <isPaused;

    var task;

    *new {arg env, action, resetOnStop=false;
        ^super.newCopyArgs(env, action, resetOnStop).init;
    }

    init {
        updateFreq = 30; // hz
        isPaused = false;
        this.create;
    }

    create {
        var val, envTime;
        var idx = 0;

        task = Task {
            loop {
                envTime = (idx/updateFreq).wrap(0, env.duration);
                val = env.at(envTime);
                action.(val);
                if (isPaused.not) {
                    idx = idx + 1;
                };
                updateFreq.reciprocal.wait;
            }
        };
    }

    play {
        if (action.isNil) {
            "No action function specified".throw;
        };

        isPaused = false;
        task.play;
    }

    stop {
        task.stop;
        if (resetOnStop) {
            this.create;
        }
    }

    pause {
        isPaused = isPaused.not;
    }
}
