# joinfive
Join five game and AI implementation with Nested Monte Carlo Search and Nested Rollout Policy Adaptation

This is a Javafx implementation of the popular <a href="https://en.wikipedia.org/wiki/Join_Five">Join Five (or Morpion Solitaire)<a> <a href="http://www.morpionsolitaire.com/English/NPHard.htm">np-hard problem<a>.
I have implemented both the common modes 5T and 5D for this. 
The user can switch between Human and Computer player. For the human player, I have added a Show Hint feature which highlights the current possible moves.
For the Computer player, I have implemented three algorithms- 
<ol><li>Random Search</li>
<li>Nested Monte Carlo Search(NMCS)</li>
<li>Nested Rollout Policy Adaptation(NRPA)</li>
</ol>

With the NMCS algorithm, it can easily outperform the results of several approaches (with a score of 100+) in old research papers. So, I'm confident that with NRPA, it will be able to provide even better results.
However, my current NRPA implementation is very slow, mainly due to the way it copies the whole grid each time while evaluating each child state. 
For now, I'll keep the core of this project backed up on github, and will come back to it later when I get some free time next year.

If you're interested in this topic for research, and can contribute in the NRPA implementation, feel free to reach out.
