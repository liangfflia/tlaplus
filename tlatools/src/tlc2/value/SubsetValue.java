// Copyright (c) 2003 Compaq Corporation.  All rights reserved.
// Portions Copyright (c) 2003 Microsoft Corporation.  All rights reserved.
// Last modified on Wed 12 Jul 2017 at 16:10:00 PST by ian morris nieves
//      modified on Mon 30 Apr 2007 at 13:46:07 PST by lamport
//      modified on Fri Aug 10 15:10:16 PDT 2001 by yuanyu

package tlc2.value;

import java.util.BitSet;

import tlc2.tool.ModelChecker;
import tlc2.tool.FingerprintException;
import tlc2.output.EC;
import util.Assert;

public class SubsetValue extends EnumerableValue implements Enumerable {
  public Value set;           // SUBSET set
  protected SetEnumValue pset;

  /* Constructor */
  public SubsetValue(Value set) {
    this.set = set;
    this.pset = null;
  }

  public final byte getKind() { return SUBSETVALUE; }

  public final int compareTo(Object obj) {
    try {
      if (obj instanceof SubsetValue) {
        return this.set.compareTo(((SubsetValue)obj).set);
      }
      this.convertAndCache();
      return this.pset.compareTo(obj);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final boolean equals(Object obj) {
    try {
      if (obj instanceof SubsetValue) {
        return this.set.equals(((SubsetValue)obj).set);
      }
      this.convertAndCache();
      return this.pset.equals(obj);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final boolean member(Value val) {
    try {
      if (val instanceof Enumerable) {
        ValueEnumeration Enum = ((Enumerable)val).elements();
        Value elem;
        while ((elem = Enum.nextElement()) != null) {
          if (!this.set.member(elem)) return false;
        }
      }
      else {
        Assert.fail("Attempted to check if the non-enumerable value\n" +
        ppr(val.toString()) + "\nis element of\n" + ppr(this.toString()));
      }
      return true;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public Value isSubsetEq(Value other) {
    try {
      // Reduce (SUBSET A \subseteq SUBSET B) to (A \subseteq B) to avoid
      // exponential blowup inherent in generating the power set.
      if (other instanceof SubsetValue && this.set instanceof EnumerableValue) {
        final SubsetValue sv = (SubsetValue) other;
        return ((EnumerableValue) this.set).isSubsetEq(sv.set);
      }
      return super.isSubsetEq(other);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final boolean isFinite() {
    try {
      return this.set.isFinite();
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final Value takeExcept(ValueExcept ex) {
    try {
      if (ex.idx < ex.path.length) {
        Assert.fail("Attempted to apply EXCEPT to the set " + ppr(this.toString()) + ".");
      }
      return ex.value;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final Value takeExcept(ValueExcept[] exs) {
    try {
      if (exs.length != 0) {
        Assert.fail("Attempted to apply EXCEPT to the set " + ppr(this.toString()) + ".");
      }
      return this;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final int size() {
    try {
      int sz = this.set.size();
      if (sz >= 31) {
        Assert.fail(EC.TLC_MODULE_OVERFLOW, "the number of elements in:\n" +
        ppr(this.toString()));
      }
      return (1 << sz);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final boolean isNormalized() {
    try {
      return (this.pset != null &&
        this.pset != DummyEnum &&
        this.pset.isNormalized());
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final void normalize() {
    try {
      if (this.pset == null || this.pset == DummyEnum) {
        this.set.normalize();
      }
      else {
        this.pset.normalize();
      }
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final boolean isDefined() {
    try {
      return this.set.isDefined();
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final Value deepCopy() { return this; }

  public final boolean assignable(Value val) {
    try {
      return this.equals(val);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  /* The fingerprint  */
  public final long fingerPrint(long fp) {
    try {
      this.convertAndCache();
      return this.pset.fingerPrint(fp);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final Value permute(MVPerm perm) {
    try {
      this.convertAndCache();
      return this.pset.permute(perm);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  private final void convertAndCache() {
    if (this.pset == null) {
      this.pset = SetEnumValue.convert(this);
    }
    else if (this.pset == DummyEnum) {
      SetEnumValue val = null;
      synchronized(this) {
        if (this.pset == DummyEnum) {
          val = SetEnumValue.convert(this);
          val.deepNormalize();
        }
      }
      synchronized(this) {
        if (this.pset == DummyEnum) { this.pset = val; }
      }
    }
  }

  /* The string representation  */
  public final StringBuffer toString(StringBuffer sb, int offset) {
    try {
      boolean unlazy = expand;
      try {
        if (unlazy) {
          unlazy = this.set.size() < 7;
        }
      }
      catch (Throwable e) { unlazy = false; }

      if (unlazy) {
        Value val = SetEnumValue.convert(this);
        return val.toString(sb, offset);
      }
      else {
        sb = sb.append("SUBSET ");
        sb = this.set.toString(sb, offset);
        return sb;
      }
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final ValueEnumeration elements() {
    try {
      if (this.pset == null || this.pset == DummyEnum) {
        return new Enumerator();
      }
      return this.pset.elements();
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  final class Enumerator implements ValueEnumeration {
    ValueVec elems;
    private BitSet descriptor;

    public Enumerator() {
      set = SetEnumValue.convert(set);
      set.normalize();
      this.elems = ((SetEnumValue)set).elems;
      this.descriptor = new BitSet(this.elems.size());
    }

    public final void reset() {
      this.descriptor = new BitSet(this.elems.size());
    }

    public final Value nextElement() {
      if (this.descriptor == null) return null;
      ValueVec vals = new ValueVec();
      int sz = this.elems.size();
      if (sz == 0) {
        this.descriptor = null;
      }
      else {
        for (int i = 0; i < sz; i++) {
          if (this.descriptor.get(i)) {
            vals.addElement(this.elems.elementAt(i));
          }
        }
        for (int i = 0; i < sz; i++) {
          if (this.descriptor.get(i)) {
            this.descriptor.clear(i);
            if (i >= sz - 1) {
              this.descriptor = null;
              break;
            }
          }
          else {
            this.descriptor.set(i);
            break;
          }
        }
      }
      return new SetEnumValue(vals, true);
    }

  }

}
