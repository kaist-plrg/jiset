package kr.ac.kaist.jiset.core

// CORE Types
case class Ty(name: String) extends CoreNode {
  override def toString: String = s"""Ty("$name")"""
}