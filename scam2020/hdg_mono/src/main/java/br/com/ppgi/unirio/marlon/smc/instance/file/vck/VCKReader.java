package br.com.ppgi.unirio.marlon.smc.instance.file.vck;

import br.com.ppgi.unirio.marlon.smc.instance.file.bunch.*;
import br.com.ppgi.unirio.marlon.smc.instance.file.InstanceParseException;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Efetua a leitura de uma instância VCK para o objeto ModuleDependencyGraph
 * @author kiko
 */
public class VCKReader{
	
        private static final String SPLITTER = "\\s+";//split por qualquer espacamento
	/*
         * Cada linha possui uma classe e sua dependencia
         * terminar com uma linha em branco
         */
        Set<String> dependencies = new HashSet<>();
	
        /**
         * Efetua a leitura de uma instancia
         * @param path
         * @return
         * @throws InstanceParseException 
         */
	public ModuleDependencyGraph execute(String path) throws InstanceParseException{
		throw new UnsupportedOperationException("NOT IMPLEMENTED");
        }
}
