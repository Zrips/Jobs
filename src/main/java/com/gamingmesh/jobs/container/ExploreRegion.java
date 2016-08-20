package com.gamingmesh.jobs.container;

import java.util.ArrayList;
import java.util.List;

public class ExploreRegion {

	int x;
	int z;
	List<ExploreChunk> chunks = new ArrayList<ExploreChunk>();

	public ExploreRegion(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public void addChunk(ExploreChunk chunk) {
		chunks.add(chunk);
	}

	public List<ExploreChunk> getChunks() {
		return chunks;
	}
}
