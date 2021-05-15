package br.unb.cic.cms.runner.algorithm;

import br.unirio.lns.hdesign.model.Project;
import jmetal.base.Algorithm;

import java.util.Vector;

public interface AlgorithmFactory  {
    Algorithm instance(Project project) throws Exception;
}
