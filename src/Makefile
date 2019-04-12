JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        BPlusTree.java \
        TreeNode.java \
        Execute.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class