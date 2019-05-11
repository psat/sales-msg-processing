package com.psat;

import com.psat.visitor.Visitor;

public interface Message {

  void accept(Visitor visitor);

}
