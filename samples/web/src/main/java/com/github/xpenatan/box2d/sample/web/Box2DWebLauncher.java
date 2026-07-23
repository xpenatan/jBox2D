package com.github.xpenatan.box2d.sample.web;

import com.github.xpenatan.box2d.sample.Box2DSampleApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import java.util.ArrayList;
import org.teavm.jso.JSBody;

public final class Box2DWebLauncher {
    private Box2DWebLauncher() {
    }

    public static void main(String[] args) {
        applyOptions(mergeArgs(args, queryArgs()));
        WebApplicationConfiguration config = new WebApplicationConfiguration("canvas");
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = true;
        config.useGL30 = true;
        new WebApplication(new Box2DSampleApplication(), config);
    }

    private static void applyOptions(String[] args) {
        if(args == null) return;
        for(String arg : args) {
            setOption(arg, "--sample=", "jbox2d.sample.sample");
            setOption(arg, "--sample-index=", "jbox2d.sample.sampleIndex");
            setOption(arg, "--exit-after-frames=", "jbox2d.sample.exitAfterFrames");
        }
    }

    private static void setOption(String arg, String prefix, String property) {
        if(arg != null && arg.startsWith(prefix)) System.setProperty(property, arg.substring(prefix.length()));
    }

    private static String[] queryArgs() {
        String query = locationSearch();
        if(query == null || query.length() <= 1) return new String[0];
        ArrayList<String> args = new ArrayList<String>();
        String[] pairs = query.substring(1).split("&");
        for(String pair : pairs) {
            int split = pair.indexOf('=');
            String name = decode(split >= 0 ? pair.substring(0, split) : pair);
            String value = decode(split >= 0 ? pair.substring(split + 1) : "true");
            if("sample".equals(name)) args.add("--sample=" + value);
            else if("sampleIndex".equals(name) || "sample-index".equals(name)) args.add("--sample-index=" + value);
            else if("exitAfterFrames".equals(name) || "exit-after-frames".equals(name)) args.add("--exit-after-frames=" + value);
        }
        return args.toArray(new String[args.size()]);
    }

    private static String[] mergeArgs(String[] first, String[] second) {
        int firstLength = first == null ? 0 : first.length;
        int secondLength = second == null ? 0 : second.length;
        String[] merged = new String[firstLength + secondLength];
        for(int i = 0; i < firstLength; i++) merged[i] = first[i];
        for(int i = 0; i < secondLength; i++) merged[firstLength + i] = second[i];
        return merged;
    }

    @JSBody(params = {"value"}, script = "return decodeURIComponent(String(value).replace(/\\+/g, ' '));")
    private static native String decode(String value);

    @JSBody(script = "return typeof window !== 'undefined' && window.location ? window.location.search : '';")
    private static native String locationSearch();
}
