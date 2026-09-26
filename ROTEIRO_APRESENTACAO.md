# Roteiro de apresentação — TP5

Duração aproximada: 8 a 10 minutos.

Antes de gravar, deixe aberto um terminal na raiz do TP5, a aplicação em http://localhost:4373, o Grafana em http://localhost:3001 e a aba Actions do repositório.

## 1. Introdução

Fala:

“Este trabalho prepara o sistema de biblioteca para operação. A solução possui uma API principal, um serviço de notificações e um frontend. Nesta etapa, os componentes foram conteinerizados, receberam manifestos Kubernetes, monitoramento centralizado, rastreamento distribuído, testes automatizados e uma pipeline de CI/CD.”

Mostre rapidamente as pastas backend, notificacoes-service, frontend, k8s, monitoring, scripts e .github/workflows.

## 2. Contêineres

Execute:

    docker compose up -d --build
    docker compose ps

Fala:

“Cada aplicação tem um Dockerfile com build em múltiplos estágios. As imagens finais executam com usuário sem privilégios e possuem health checks. O Compose também inicia dois bancos PostgreSQL independentes, RabbitMQ e a estrutura de observabilidade.”

Mostre que backend, notificações, frontend, bancos e RabbitMQ estão ativos e saudáveis.

## 3. Funcionamento integrado

Abra http://localhost:4373. Cadastre um leitor, um livro e um empréstimo. Abra as notificações do leitor, registre a devolução e abra novamente as notificações.

Fala:

“O frontend chama a API pelo proxy do Nginx. Ao registrar o empréstimo, a API salva a operação e um evento na Outbox. O evento é publicado no RabbitMQ e consumido pelo serviço de notificações. A devolução percorre o mesmo fluxo. Assim, os serviços mantêm bancos separados e a integração de escrita continua assíncrona.”

Mostre o RabbitMQ em http://localhost:15673, usando biblioteca / biblioteca, e abra a fila notificacoes.emprestimos.v1.

## 4. Monitoramento

Abra o Grafana em http://localhost:3001, usando admin / admin. Em Explore, selecione Prometheus e execute as consultas up e http_server_requests_seconds_count.

Fala:

“O Spring Boot Actuator fornece health checks e métricas. O Prometheus coleta os dados dos dois microsserviços, permitindo acompanhar disponibilidade, requisições HTTP, JVM e outros indicadores.”

Selecione Loki e execute:

    {service=~"backend|notificacoes-service"}

Expanda uma linha que tenha traceId.

Fala:

“O Promtail coleta os logs dos contêineres e envia ao Loki. Os registros possuem o identificador do trace e do span, o que permite correlacionar uma falha com a transação que a originou.”

Selecione Tempo, escolha Search, pesquise pelos serviços biblioteca e notificacoes-service e abra um trace HTTP.

Fala:

“Os traces são gerados com Micrometer e OpenTelemetry e enviados ao Tempo por OTLP. Aqui é possível visualizar a duração e os spans de cada requisição. O Grafana relaciona logs e traces pelo mesmo traceId.”

## 5. Testes

Execute:

    cd backend && mvn test
    cd ../notificacoes-service && mvn test
    cd ..
    ./scripts/smoke-test.sh

Fala:

“Os testes abrangem serviços, persistência, histórico, endpoints web, Outbox e consumo idempotente. O smoke test executa um cenário real: inicia o ambiente, cadastra leitor e livro, registra empréstimo e devolução, aguarda os eventos no serviço de notificações e verifica as métricas.”

## 6. Kubernetes

Se estiver demonstrando com Minikube, encerre o Compose antes para liberar recursos:

    docker compose down
    kubectl apply -k k8s
    kubectl -n biblioteca get pods
    kubectl -n biblioteca get deployments,statefulsets
    kubectl -n biblioteca get hpa,pdb

Fala:

“No Kubernetes, os componentes de dados usam StatefulSets e os serviços da aplicação usam Deployments. As aplicações começam com duas réplicas, têm probes, limites de recursos e políticas de disponibilidade. Backend e notificações possuem HPA entre duas e cinco réplicas, com alvo de 70% de CPU.”

Demonstre uma alteração de escala:

    kubectl -n biblioteca scale deployment/backend --replicas=3
    kubectl -n biblioteca rollout status deployment/backend
    kubectl -n biblioteca get pods -l app=backend

Fala:

“A implantação mantém a aplicação disponível durante mudanças de réplica. Em operação, o HPA usa as métricas do cluster para ajustar automaticamente a capacidade.”

## 7. GitHub Actions

Abra .github/workflows/ci-cd.yml e depois a execução na aba Actions do GitHub.

Fala:

“A integração contínua roda em pushes e pull requests. Ela testa os dois serviços, gera o frontend, valida os manifestos Kubernetes e executa o teste ponta a ponta com Docker Compose. Depois do sucesso, três imagens versionadas pelo commit são publicadas no GitHub Container Registry.”

Mostre os jobs concluídos e, se possível, os três packages no GHCR.

Fala:

“O job de entrega Kubernetes é opcional. Quando a variável ENABLE_KUBERNETES_DEPLOY e o secret KUBE_CONFIG são configurados, o workflow aplica os manifestos, atualiza as imagens e aguarda o rollout. Sem um cluster configurado, apenas esse job é ignorado.”

## 8. Encerramento

Fala:

“Com isso, o sistema pode ser reproduzido em Docker, orquestrado e escalado no Kubernetes, observado por métricas, logs e traces, e validado automaticamente a cada mudança. A documentação do repositório reúne os comandos de implantação, monitoramento, testes e configuração da pipeline.”
