# grasp-repo
Code for experimental graphical SPARQL query builder.

GRASP (GRAphical SParql builder) is written in Java and is built using the Eclipse plugin framework.  Drawing on GEF (legacy), rdf4j and OWL Ontology representations
(in particular HermiT) it provides a graphical environment where users can create SPARQL queries that align with a given OWL Ontology.  Queries built graphically
are saved in a JSON-based file format, but can be translated into standard SPARQL and run against a repository of data.

There are four Eclipse projects in this repo.  Two of them are provided to support both workspace-oriented file access from within Eclipse and standard file
access for a standalone version of GRASP:

* uk.ac.kcl.cch.jb.sparql: the bulk of the code resides here.
* uk.ac.kcl.cch.jb.sparql.ext: provides a small amount of code that allows GRASP to work with the operating system's standard file access mechanisms.
* uk.ac.kcl.cch.jb.sparql.rcp: provides an RCP wrapper around the GRASP code to allow the tool to operate standalone; outside of Eclipse.
* uk.ac.kcl.cch.jb.sparql.wbk: provides a small amount of code that allows GRASP to work within the Eclipse Workbench file access mechanisms.

This code is made available through the Eclipse Public License - v 2.0