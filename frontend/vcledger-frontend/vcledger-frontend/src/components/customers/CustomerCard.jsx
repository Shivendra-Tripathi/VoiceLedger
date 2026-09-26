
import { useNavigate } from 'react-router-dom';
import { Phone } from 'lucide-react';
import Avatar from '../common/Avatar';
import { formatCurrency } from '../../utils/formatters';

/**
 * CustomerCard
 * -------------
 * One tile in AllCustomersPage's grid: photo/initials, name, phone, and
 * balance.
 *
 * parameter format :
 *      customer : CustomerBalanceInfo {
 *          customer : PersonInfo {
 *              id : number,
 *              name : string,
 *              photoUrl : string | null,
 *              type : PersonType {
 *                  CUSTOMER, SHOPKEEPER
 *              }
 *          },
 *          balance : number
 *      }
 */
export default function CustomerCard({ customer:customerInfo }) {

  const { customer, balance } = customerInfo;
  const navigate = useNavigate();

  const hasBalance = typeof balance === 'number' && balance !== 0;
  const customerOwes = balance < 0;

  return (
    <button
      type="button"
      onClick={() => navigate(`/customers/${customer.id}`)}
      className="surface-card flex flex-col items-center text-center gap-2.5 p-5 hover:-translate-y-0.5 hover:shadow-raised transition-transform duration-150 text-left"
    >
      <Avatar
        name={customer.name}
        photoUrl={customer.photoUrl}
        size="lg"
      />

      <p className="font-display font-semibold text-ink leading-tight">
        {customer.name}
      </p>

      {/* Phone — currently unavailable from PersonInfo */}
      {/* {personInfo.phone && (
        <p className="flex items-center gap-1.5 text-xs text-ink-soft">
          <Phone size={12} />
          {personInfo.phone}
        </p>
      )} */}

      <div className="brass-divider w-16 my-1" />

      {hasBalance && (
        <p
          className={`font-body font-bold text-sm ${
            customerOwes ? 'text-debit' : 'text-credit'
          }`}
        >
          {formatCurrency(balance>0 ? balance : -balance)}

          <span className="block text-[10px] font-medium text-ink-soft mt-0.5">
            {customerOwes ? 'owes' : 'advance paid'}
          </span>
        </p>
      )}
    </button>
  );
}


// The resulting behavior is:

// |              Balance | Display                   |
// | -------------------: | ------------------------- |
// |               `-500` | `₹500` + **owes**         |
// |                `500` | `₹500` + **advance paid** |
// |                  `0` | **Nothing**               |
// | `null` / `undefined` | **Nothing**               |

// One small point: this assumes your API's negative balance means **customer owes the shopkeeper**, as in your VcLedger balance convention.
