package com.gamingmesh.jobs.dao;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import com.gamingmesh.jobs.Jobs;

public class JobsClassLoader extends URLClassLoader {

    public JobsClassLoader(Jobs core) {
	super(new URL[0], core.getClass().getClassLoader());
    }

    public void addFile(File f) throws IOException {
	addURL(f.toURI().toURL());
    }

    @Override
    public void addURL(URL url) {
	for (URL u : getURLs())
	    if (url.sameFile(u)) return;

	super.addURL(url);
    }
}
