package uk.ac.kcl.cch.jb.sparql.commands;

public interface CanBeDirty {
   public boolean isDirty();
   public void setDirtyFlag(boolean val);
   public void markDirty();
   public void fireDirtyEvent();
}
