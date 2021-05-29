INSERT INTO usuario (codigo, nome, email, senha) values (1, 'Administrador', 'admin@desafioaws.com', '$2a$10$.QV0PQoNbGSeaOsqtFYCuuol1Z6ivLLuk0Rb97wUnmI3Bzbeg9Pre');
INSERT INTO usuario (codigo, nome, email, senha) values (2, 'John Tester', 'johntester@desafioaws.com', '$2a$10$Zc3w6HyuPOPXamaMhh.PQOXvDnEsadztbfi6/RyZWJDzimE8WQjaq');
INSERT INTO usuario (codigo, nome, email, senha) values (3, 'Delson 2004', 'delson2004@yahoo.com.br', '$2a$10$zbIfy5IkSsnR4Ttb.hka5OR08YaYHe2/juhxDv5Gx6fEbyM4MDEZi');


INSERT INTO permissao (codigo, descricao) values (1, 'ROLE_CADASTRAR_USUARIO');
INSERT INTO permissao (codigo, descricao) values (2, 'ROLE_REMOVER_USUARIO');
INSERT INTO permissao (codigo, descricao) values (3, 'ROLE_PESQUISAR_USUARIO');

INSERT INTO permissao (codigo, descricao) values (4, 'ROLE_UPLOAD_ARQUIVO');
INSERT INTO permissao (codigo, descricao) values (5, 'ROLE_REMOVER_ARQUIVO');
INSERT INTO permissao (codigo, descricao) values (6, 'ROLE_PESQUISAR_ARQUIVO');

-- Administrador
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (1, 1);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (1, 2);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (1, 3);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (1, 4);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (1, 5);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (1, 6);

-- John
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (2, 4);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (2, 5);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (2, 6);


INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (3, 1);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (3, 2);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (3, 3);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (3, 4);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (3, 5);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (3, 6);