We provide all attack trees and their extension (in the following just
AT for short) as .dot-files, with the following structure: Each file
contains a single AT and a single directed graph declared as follows:


digraph {
  // attack tree goes here
}

NODES
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

We define nodes as usual in dot (with an id, in all examples 0) and
add all information necessary to assign gates, cost etc. as attributes
in brackets. Each node needs to have an attribute "operator", which
determines whether a node represents a basic event or an operator. 

* Basic Events:
  0 [operator="BE",name="5",distribution="Defender",cost="80.0",probability="0.4"];
  
  The attribute distribution can have "Attacker", "Defender" or a
  notion of a distribution like "Uniform(10,17)" to denote an
  attacker- or defender-controlled or a timed basic event (execution
  driven by a distribution over time). The attribute "cost" denotes
  the cost of executing the event and the attribute "probability" the
  success probability. Nodes representing basic events must not have
  any predecessor.

* An
  0 [operator="AND"];

  Nodes with operator AND must have at least two predecessors.


* Or
  0 [operator="OR"];

  Nodes with operator OR must have at least two predecessors.

* SAnd
  0 [operator="SAND", order="1,2"];
  
  The attribute "order" needs to be assigned a list of nodes, which
  specifies the order of its predecessors. For each id in order, there
  must exist a node with the respective id and a transition from these
  nodes to this one. In our example, the nodes with id 1 and 2 and the
  transitions 1->0; and 2->0; must exist. Nodes with operator SAND
  must have at least two predecessors.


* SOr
  0 [operator="SOR", order="1,2"];

  Nodes with operator SOR must have at least two predecessors.

* Negation
  0 [operator="NOT"];

  Nodes with operator NOT  must have exactly one predecessor.

  
* Invert (3V logic operator only)
  0 [operator="IVR"];

  Nodes with operator IVR  must have exactly one predecessor.

  
* Trigger
  0 [operator="TRIGGER"];

  Nodes with operator TRIGGER  must have exactly one predecessor.

* Reset
  0 [operator="RESET"];

  Nodes with operator RESET  must have exactly one predecessor.

* If
  0 [operator="IF", first="1", second="2"];

* Cost
  0 [operator="COST",bound="10.0",comparison="<="];
  
  Nodes with operator COST must have exactly one predecessor. The
  attribute "bound" denotes the right operand of the comparision
  declared in the attribute "comparison". For the attribute
  "comparison", we allow the operators <, >, <=, =, =>. 

 The attributes "first" and "second" speficy, which predecessor is the first
 and which one is the second. These nodes must have exactly two predecessors.

The attribute "sink" can be added to any node. Its value should either be "Attacker" or "Defender". It denotes whether a node is the goal of the attacker or the defender. All other attributes assigned to nodes will be ignored.

EDGES
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

We specify edges as usual in as 1->2; were 1 here denotes the start
and 2 the end point. We only consider predecessors w.r.t. to edges,
which do not have the attribute "type". The attribute "type" is used
to denote trigger and reset edges:

* Trigger edges:
  70->31[type="trigger"];
   
  This example denotes a trigger edge from node 70 to node 31. Node 70
  must assign TRIGGER to the attribute "operator", while 31 must
  represent a basic event.

* Reset edges:
  70->31[type="reset"];

  This example denotes a reset edge from node 70 to node 31. Node 70
  must assign RESET to the attribute "operator", while 31 must
  represent a basic event.

All other attributes assigned to edges will be ignored.


EXAMPLE
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

The example code for the attack defence on
https://www7.in.tum.de/~kraemerj/upload/anonymisation.htm (without
coloring options):

digraph {
11 [operator="OR"];
12 [operator="TRIGGER"];
13 [operator="OR"];
14 [operator="COST",bound="10.0",comparison="<="];
15 [operator="And"];
16 [operator="TRIGGER"];
17 [operator="IVR"];
18 [operator="Neg"];
19 [operator="OR"];
1 [operator="BE",name="Find Valuable Subject",color=red,costExecuting="2.0",costDelaying="0.0",probability="1.0"];
2 [operator="BE",name="Guess Password",color=red,costExecuting="0.0",costDelaying="0.0",probability="0.000000000001"];
3 [operator="BE",name="Install Keylogger",color=red,costExecuting="4.0",costDelaying="0.0",probability="0.5"];
4 [operator="BE",name="Get Correct Password",color=red,costExecuting="1.0",costDelaying="0.0",probability="0.6"];
5 [operator="BE",name="Get Physical Access",color=red,costExecuting="5.0",costDelaying="0.0",probability="0.5"];
6 [operator="BE",name="Check for Malware",distribution="Uniform(4.95,5.05)",costExecuting="0.0",costDelaying="0.0",probability="1.0"];
7 [operator="BE",name="Send Embarrassing Mails",color=red,costExecuting="0.0",costDelaying="",probability="1.0"];
8 [operator="BE",name="Block Infected Accounts",color=green,shape=box,costExecuting="0.0",costDelaying="0.0",probability="0.9"];
9 [operator="TRIGGER"];
20 [operator="IF",first="8",second="19",sink="Defender"];
10 [operator="RESET"];

11->13;
12->8[type="trigger"];
13->14;
14->15;
15->16;
16->7[type="trigger"];
17->19;
18->19;
19->20;
1->15;
2->11;
3->9;
4->11;
5->12;
6->10;
7->17;
7->18;
8->20;
9->4[type="trigger"];
10->6[type="reset"];
10->3[type="reset"];
}
