package br.com.fantinel.jboss.as.controller.values;

public enum IsolationLevel {

	/**
	 * Nenhum suporte de transação é necessário. Não há bloqueio nesse nível, por
	 * exemplo, os usuários precisarão gerenciar a integridade dos dados.
	 * Implementações não usam bloqueios.
	 */
	NONE,

	/**
	 * Os dados podem ser lidos a qualquer momento, enquanto as operações de
	 * gravação são exclusivas. Note que este nível não impede a chamada "leitura
	 * suja", onde os dados modificados em Tx1 podem ser lidos em Tx2 antes dos
	 * commits do Tx1.
	 */
	READ_UNCOMMITTED,

	/**
	 * Os dados podem ser lidos a qualquer momento, desde que não haja gravação.
	 * Este nível impede a leitura suja. Mas isso não impede que a chamada "leitura
	 * não repetível", em que um segmento lê os dados duas vezes, podendo produzir
	 * resultados diferentes.
	 */
	READ_COMMITTED,

	/**
	 * Os dados podem ser lidos enquanto não houver gravação e vice-versa. Esse
	 * nível impede a "leitura não repetível", mas não impede completamente a
	 * chamada "leitura fantasma", na qual novos dados podem ser inseridos na árvore
	 * a partir de outra transação. Implementações geralmente usam um bloqueio de
	 * leitura / gravação. Este é o nível de isolamento padrão usado.
	 */
	REPEATABLE_READ,

	/**
	 * O acesso a dados é sincronizado com bloqueios exclusivos. Apenas um escritor
	 * ou leitor pode ter o bloqueio a qualquer momento. Os bloqueios são liberados
	 * no final da transação. Considerado como muito ruim para desempenho e
	 * simultaneidade de segmento / transação.
	 */
	SERIALIZABLE;
}
