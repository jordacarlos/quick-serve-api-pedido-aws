import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
  vus: 10000,
  duration: '3s',
};

export default function () {
  http.get('http://localhost:30081/quick_service/customers');
  sleep(1);
}
