package edu.columbia.cs.utils;

/* Copyright (c) 2011 the authors listed at the following URL, and/or
the authors of referenced articles or incorporated external code:
http://en.literateprograms.org/Dijkstra's_algorithm_(Java)?action=history&offset=20081113161332

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Retrieved from: http://en.literateprograms.org/Dijkstra's_algorithm_(Java)?oldid=15444
 */

import java.util.PriorityQueue;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


class Vertex implements Comparable<Vertex>
{
	public final String[] name;
	public List<Edge> adjacencies = new ArrayList<Edge>();
	public double minDistance = Double.POSITIVE_INFINITY;
	public Vertex previous;
	public Edge previousPath;
	public int id;
	public Vertex(int argId, String[] argName) { name = argName; id = argId; }
	public String toString() { return Arrays.toString(name); }
	public int compareTo(Vertex other)
	{
		return Double.compare(minDistance, other.minDistance);
	}

	public void addEdge(Edge e){
		adjacencies.add(e);
	}
}


class Edge
{
	public final Vertex origin;
	public final Vertex target;
	public final double weight;
	public boolean sign;
	public Edge(Vertex argOrigin, Vertex argTarget, double argWeight, boolean argSign)
	{ origin = argOrigin; target = argTarget; weight = argWeight; sign=argSign;}
}


public class Dijkstra
{
	public static void computePaths(Vertex source)
	{
		source.minDistance = 0.;
		PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
		vertexQueue.add(source);

		while (!vertexQueue.isEmpty()) {
			Vertex u = vertexQueue.poll();

			// Visit each edge exiting u
			for (Edge e : u.adjacencies)
			{
				Vertex v = e.target;
				double weight = e.weight;
				double distanceThroughU = u.minDistance + weight;
				if (distanceThroughU < v.minDistance) {
					vertexQueue.remove(v);

					v.minDistance = distanceThroughU ;
					v.previous = u;
					v.previousPath=e;
					vertexQueue.add(v);

				}

			}
		}
	}


	public static List<Edge> getShortestPathTo(Vertex target)
	{
		List<Edge> path = new ArrayList<Edge>();
		for (Vertex vertex = target; vertex != null; vertex = vertex.previous){
			if(vertex.previousPath!=null){
				path.add(vertex.previousPath);
			}
		}

		Collections.reverse(path);
		return path;
	}


}

