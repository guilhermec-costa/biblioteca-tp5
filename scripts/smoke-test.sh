#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$project_dir"

wait_for_url() {
  local url="$1"
  local name="$2"
  for attempt in $(seq 1 90); do
    if curl -fsS "$url" >/dev/null; then
      printf '%s disponível.\n' "$name"
      return 0
    fi
    sleep 2
  done
  printf '%s não ficou disponível no tempo esperado.\n' "$name" >&2
  return 1
}

docker compose up -d --build

wait_for_url "http://localhost:8380/actuator/health/readiness" "Backend"
wait_for_url "http://localhost:8381/actuator/health/readiness" "Notificações"
wait_for_url "http://localhost:4373/" "Frontend"
wait_for_url "http://localhost:9091/-/ready" "Prometheus"
wait_for_url "http://localhost:3100/ready" "Loki"
wait_for_url "http://localhost:3200/ready" "Tempo"
wait_for_url "http://localhost:3001/api/health" "Grafana"

suffix="$(date +%s)"
reader_response="$(curl -fsS -X POST http://localhost:4373/api/leitores \
  -H 'Content-Type: application/json' \
  -d "{\"nome\":\"Teste Integrado\",\"email\":\"integracao-$suffix@example.com\"}")"
reader_id="$(printf '%s' "$reader_response" | sed -E 's/.*"id":([0-9]+).*/\1/')"

book_response="$(curl -fsS -X POST http://localhost:4373/api/livros \
  -H 'Content-Type: application/json' \
  -d "{\"titulo\":\"Operação em Produção\",\"autor\":\"Equipe Biblioteca\",\"isbn\":\"TP5-$suffix\"}")"
book_id="$(printf '%s' "$book_response" | sed -E 's/.*"id":([0-9]+).*/\1/')"

loan_response="$(curl -fsS -X POST http://localhost:4373/api/emprestimos \
  -H 'Content-Type: application/json' \
  -d "{\"livroId\":$book_id,\"leitorId\":$reader_id,\"dataPrevistaDevolucao\":\"2027-12-31\"}")"
loan_id="$(printf '%s' "$loan_response" | sed -E 's/.*"id":([0-9]+).*/\1/')"

notifications="[]"
for attempt in $(seq 1 30); do
  notifications="$(curl -fsS "http://localhost:4373/api/leitores/$reader_id/notificacoes")"
  if printf '%s' "$notifications" | grep -q 'EMPRESTIMO_REGISTRADO'; then
    break
  fi
  sleep 1
done
printf '%s' "$notifications" | grep -q 'EMPRESTIMO_REGISTRADO'

curl -fsS -X POST "http://localhost:4373/api/emprestimos/$loan_id/devolucao" >/dev/null

for attempt in $(seq 1 30); do
  notifications="$(curl -fsS "http://localhost:4373/api/leitores/$reader_id/notificacoes")"
  if printf '%s' "$notifications" | grep -q 'DEVOLUCAO_REGISTRADA'; then
    break
  fi
  sleep 1
done
printf '%s' "$notifications" | grep -q 'DEVOLUCAO_REGISTRADA'
curl -fsS http://localhost:8380/actuator/prometheus | grep -q 'http_server_requests'
curl -fsS http://localhost:8381/actuator/prometheus | grep -q 'jvm_memory'

printf 'Fluxo ponta a ponta concluído: leitor=%s livro=%s empréstimo=%s.\n' "$reader_id" "$book_id" "$loan_id"
