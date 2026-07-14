package com.github.xpenatan.box2d;

import com.github.xpenatan.jParser.loader.JParserLibraryLoader;
import com.github.xpenatan.jParser.loader.JParserLibraryLoaderListener;
import com.github.xpenatan.jparser.runtime.RuntimeLoader;

/** Loads the generated jBox2D runtime for the active platform. */
public final class JBox2DLoader {

    /*[-JNI;-NATIVE]
        #include "jBox2D.h"
    */

    /*[-FFM;-NATIVE]
        #include "jBox2D.h"
    */

    private JBox2DLoader() {
    }

    public static void init(JParserLibraryLoaderListener listener) {
        RuntimeLoader.init(new JParserLibraryLoaderListener() {
            @Override
            public void onLoad(boolean runtimeLoaded, Throwable runtimeError) {
                if(runtimeLoaded) {
                    JParserLibraryLoader.load("box2d", listener);
                }
                else {
                    listener.onLoad(false, runtimeError);
                }
            }
        });
    }
}
