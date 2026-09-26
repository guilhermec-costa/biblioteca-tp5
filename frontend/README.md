# Frontend

Interface React para cadastro de livros e leitores, empréstimos, devoluções, históricos e consulta de notificações.

## Desenvolvimento

```bash
npm ci
npm run dev
```

O Vite encaminha `/api` para o backend configurado no ambiente de desenvolvimento.

## Contêiner

O build de produção é servido por Nginx sem privilégios na porta 8080. O Nginx também encaminha `/api` para a API principal, permitindo que o navegador use a mesma origem.

```bash
npm run build
```
