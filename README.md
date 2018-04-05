# julio-proyecto-cliente-dns-java-ro9-215

Os membros deste grupo son: Tomás Prado Rial e Adrián Iglesias Penido.

Implementacións de melloras: Proxecto completo, con todas as melloras que se mandan nos pdf.

XULLO:
1. Implementación (y uso) de una caché con todas las respuestas obtenidas
2. Posibilidad de elegir el protocolo de transporte por parte del usuario
3. Consultas sobre el RRTYPE AAAA
4. Consultas sobre otros RRTYPE (cuántos más, mejor): SOA, TXT, CNAME, ...
5. Soporte de respuestas truncadas. De encontrarse con una respuesta truncada al emplear UDP, el
cliente deberá reintentar la consulta empleando el protocolo TCP
