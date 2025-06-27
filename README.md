# 🎓 Sistema de Eventos - UFMS

> Sistema desktop para gerenciamento de eventos acadêmicos desenvolvido em JavaFX para a disciplina de Análise e Projeto de Sistemas Orientados a Objetos (APSOO).

Este projeto simula um portal onde organizadores podem submeter eventos para aprovação, e participantes podem se inscrever nas atividades oferecidas.

## 📋 Índice

- [📝 Descrição](#-descrição)
- [✨ Funcionalidades Principais](#-funcionalidades-principais)
- [💻 Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [🚀 Como Executar o Projeto](#-como-executar-o-projeto)
  - [Pré-requisitos](#pré-requisitos)
  - [Configuração do Banco de Dados](#configuração-do-banco-de-dados)
  - [Instalação e Execução](#instalação-e-execução)
- [🖼️ Exemplos de Uso](#️-exemplos-de-uso)
  - [Visão do Administrador](#visão-do-administrador)
  - [Visão do Organizador](#visão-do-organizador)
  - [Visão do Participante](#visão-do-participante)
- [📂 Estrutura do Projeto](#-estrutura-do-projeto)
- [🛠️ Arquitetura](#️-arquitetura)
- [🔐 Autenticação e Perfis](#-autenticação-e-perfis)
- [📸 Screenshots](#-screenshots)
- [🤝 Contribuindo](#-contribuindo)
- [📄 Licença](#-licença)
- [👨‍💻 Autora](#-autora)

---

## 📝 Descrição

O **Sistema de Eventos UFMS** é uma aplicação desktop desenvolvida em **JavaFX** que serve como uma plataforma centralizada para a divulgação e gerenciamento de eventos acadêmicos. O sistema foi construído seguindo os princípios de Orientação a Objetos e uma arquitetura em camadas (UI, Controller, Service, Repository) para garantir manutenibilidade e escalabilidade.

A aplicação permite que diferentes perfis de usuários (Administradores, Organizadores e Participantes) interajam com o sistema de acordo com suas permissões, criando um fluxo completo desde a solicitação de um novo evento até a inscrição de um participante em uma atividade.

---

## ✨ Funcionalidades Principais

### 🔐 Controle de Acesso
- **Administrador**: Aprova ou rejeita eventos pendentes
- **Organizador**: Submete novos eventos e suas respectivas ações para análise
- **Participante**: Visualiza eventos ativos, se inscreve em ações e gerencia suas presenças

### 📅 Gerenciamento de Eventos
- **Criação de Eventos em Múltiplos Passos**: Fluxo transacional para criar um evento, adicionar múltiplas ações (palestras, workshops, etc.)
- **Upload de Imagens**: Permite upload de imagens para eventos a partir do computador local
- **Busca e Filtro**: Busca por texto e filtro avançado por categoria, departamento e modalidade

### 📝 Formulários Dinâmicos
- Criação de formulários de inscrição personalizados para cada ação
- Campos padrão (Nome, Email, CPF, Curso) e campos personalizados configuráveis
- Validação automática de dados

### 👥 Gerenciamento de Participantes
- **Controle de Vagas**: Gerenciamento automático de vagas por ação
- **Avisos Visuais**: Indicadores de "Últimas Vagas" e "Lotado"
- **Bloqueio Automático**: Prevenção de inscrições em ações lotadas

### 🎨 Interface Responsiva
- Componentes reutilizáveis como a barra de navegação (`Homebar`)
- Adaptação dinâmica ao perfil do usuário logado
- Design intuitivo e moderno

---

## 💻 Tecnologias Utilizadas

| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| **Java** | 21+ | Linguagem de programação principal |
| **JavaFX** | 21+ | Framework para interface gráfica |
| **PostgreSQL** | Latest | Sistema de gerenciamento de banco de dados |
| **JDBC** | - | Driver para conexão com PostgreSQL |
| **Apache Maven** | 3.8+ | Ferramenta de build e gerenciamento de dependências |
| **Lombok** | Latest | Biblioteca para redução de código boilerplate |

---

## 🚀 Como Executar o Projeto

### Pré-requisitos

Certifique-se de ter instalado em sua máquina:

- ☕ [JDK (Java Development Kit)](https://www.oracle.com/java/technologies/downloads/) - **Versão 21 ou superior**
- 📦 [Apache Maven](https://maven.apache.org/download.cgi) - Versão 3.8 ou superior
- 🐘 [PostgreSQL](https://www.postgresql.org/download/) - Versão mais recente
- 💻 IDE de sua preferência (IntelliJ IDEA, Eclipse, VS Code)

### Configuração do Banco de Dados

1. **Criar o banco de dados:**
   ```sql
   CREATE DATABASE dbeventos;
   ```

2. **Configurar credenciais:**
   - Verifique o arquivo `src/main/java/com/ufms/eventos/util/ConnectionFactory.java`
   - Credenciais padrão:
     - **Usuário**: `postgres`
     - **Senha**: `123`
     - **Banco**: `dbeventos`

3. **Inicializar tabelas:**
   - Execute a classe `ConnectionFactory.java` uma vez
   - As tabelas serão criadas automaticamente

### Instalação e Execução

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/seu-usuario/eventos-ufms.git
   cd eventos-ufms
   ```

2. **Compile o projeto:**
   ```bash
   mvn clean install
   ```

3. **Execute a aplicação:**
   ```bash
   mvn javafx:run
   ```

4. **Login inicial:**
   - **Administrador**: usuário `admin`, senha `admin`
   - **Outros usuários**: criar conta através da tela de cadastro

---

## 🖼️ Exemplos de Uso

### Visão do Administrador
Como administrador, você tem acesso completo ao sistema:
- Análise e aprovação/rejeição de eventos pendentes
- Visualização de todos os eventos do sistema
- Acesso a relatórios e estatísticas

### Visão do Organizador
Como organizador, você pode:
- Criar novos eventos através do fluxo de múltiplos passos
- Gerenciar suas próprias submissões
- Visualizar inscrições em seus eventos
- Fazer upload de imagens promocionais

### Visão do Participante
Como participante, você pode:
- Navegar e filtrar eventos disponíveis
- Visualizar detalhes completos dos eventos
- Se inscrever em ações de seu interesse
- Gerenciar suas inscrições ativas

---

## 📂 Estrutura do Projeto

```
src/main/java/com/ufms/eventos/
├── 🎨 ui/              # Controllers do JavaFX (FXML)
├── 🎯 controller/      # Fachadas de lógica de negócio
├── ⚙️ services/        # Lógica de negócio principal
├── 💾 repository/      # Acesso a dados (JDBC)
├── 📊 model/           # Entidades do domínio
├── 🔄 dto/             # Data Transfer Objects
└── 🛠️ util/            # Classes utilitárias
```

---

## 🛠️ Arquitetura

O sistema segue uma **arquitetura em camadas** bem definida:

### Camada de Apresentação (UI)
- Controllers JavaFX
- Arquivos FXML
- Componentes reutilizáveis

### Camada de Controle
- Fachadas para lógica de negócio
- Validações de entrada
- Mapeamento de DTOs

### Camada de Serviço
- Regras de negócio
- Coordenação de operações
- Transações

### Camada de Persistência
- Repositórios
- Mapeamento objeto-relacional
- Gerenciamento de conexões

---

## 🔐 Autenticação e Perfis

### Perfis de Usuário

| Perfil | Permissões |
|--------|------------|
| **👑 Administrador** | Aprovar/rejeitar eventos, visualizar todos os dados |
| **📋 Organizador** | Criar eventos, gerenciar próprias submissões |
| **👤 Participante** | Visualizar eventos, fazer inscrições |

### Fluxo de Autenticação
1. Login através da tela inicial
2. Validação de credenciais no banco
3. Criação de sessão com perfil do usuário
4. Redirecionamento para tela apropriada

---

## 📸 Screenshots

![Captura de tela 2025-06-27 183330](https://github.com/user-attachments/assets/a2822b71-f42b-479b-af92-2bf5090a5a91)
![Captura de tela 2025-06-27 183251](https://github.com/user-attachments/assets/9b29bd52-6b08-4c69-b043-4c0aff8c9f0b)
![Captura de tela 2025-06-27 183230](https://github.com/user-attachments/assets/1df32de2-9b06-4914-b4f1-06f8e89bae0d)
![Captura de tela 2025-06-27 183155](https://github.com/user-attachments/assets/83548b5f-e7f2-4e2f-aab8-a3789d65790e)
![Captura de tela 2025-06-27 183230](https://github.com/user-attachments/assets/c235524b-1ddd-4f32-b2df-e5a8bd3a6669)


---

## 🤝 Contribuindo

Contribuições são sempre bem-vindas! Para contribuir:

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 👨‍💻 Autoras

**Amanda**
- 🔗 GitHub: [@amanda-bea](https://github.com/amanda-bea)

**Ana Otonni**
- 🔗 GitHub: [@anaottoni](https://github.com/anaottoni)

**Isabela Lopes**
- 🔗 GitHub: [@Lopeszs](https://github.com/Lopeszs)

**Maria Eduarda**
- 🔗 GitHub: [@MaduGoncalves](https://github.com/MaduGoncalves)


---

<div align="center">

**Desenvolvido com ❤️ para a UFMS**

⭐ **Se este projeto foi útil, considere dar uma estrela!** ⭐

</div>
