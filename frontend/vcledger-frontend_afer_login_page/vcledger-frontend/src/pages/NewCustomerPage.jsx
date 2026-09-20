import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { UserPlus, ImagePlus, Camera } from 'lucide-react';
import AppShell from '../components/layout/AppShell';
import Button from '../components/common/Button';
import { createCustomer } from '../api/customerService';

/**
 * NewCustomerPage
 * -----------------
 * Form to create a customer (spec item 2). Only name + phone are sent to
 * the backend today, matching your POST /customers contract. The image
 * picker is present and lets the shopkeeper choose a preview photo, but
 * it's intentionally NOT uploaded anywhere yet — you said you'll wire
 * that up later, so `imageFile` is kept in local state only, clearly
 * marked below, ready for you to attach to the request once that's ready.
 */
export default function NewCustomerPage() {
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');
  const [imageFile, setImageFile] = useState(null); // TODO: not yet sent to backend
  const [imagePreview, setImagePreview] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formError, setFormError] = useState(null);

  const handleImageChange = (event) => {
    const file = event.target.files?.[0];
    if (!file) return;
    setImageFile(file);
    setImagePreview(URL.createObjectURL(file));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setFormError(null);

    if (!name.trim() || !phone.trim()) {
      setFormError('Name and phone number are both required.');
      return;
    }

    setIsSubmitting(true);
    try {
      const customer = await createCustomer({ name: name.trim(), phone: phone.trim() });
      navigate(`/customers/${customer.id}`);
    } catch (err) {
      setFormError('Could not create the customer. Check that the backend is running.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <AppShell>
      <div className="max-w-lg mx-auto px-4 sm:px-8 py-10">
        <div className="flex items-center gap-3 mb-6">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-b from-brass-light to-brass flex items-center justify-center shadow-raised-sm border border-brass-dark/40">
            <UserPlus size={19} className="text-maroon-dark" />
          </div>
          <div>
            <h1 className="font-display text-xl font-semibold text-ink">New Customer</h1>
            <p className="text-xs text-ink-soft">Add someone to the ledger</p>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="surface-card p-6 sm:p-8 space-y-6">
          {/* Photo picker — preview-only for now, see file header note */}
          <div className="flex flex-col items-center gap-2">
            <label
              htmlFor="customer-photo"
              className="relative w-24 h-24 rounded-full border-2 border-dashed border-brass/70 bg-paper flex items-center justify-center cursor-pointer overflow-hidden shadow-pressed hover:border-brass transition-colors"
            >
              {imagePreview ? (
                <img src={imagePreview} alt="Customer preview" className="w-full h-full object-cover" />
              ) : (
                <Camera size={24} className="text-brass-dark" strokeWidth={1.75} />
              )}
              <input id="customer-photo" type="file" accept="image/*" onChange={handleImageChange} className="hidden" />
            </label>
            <p className="text-[11px] text-ink-soft flex items-center gap-1">
              <ImagePlus size={12} /> Optional — coming soon
            </p>
          </div>

          <div>
            <label htmlFor="customer-name" className="block text-sm font-semibold text-ink mb-1.5">
              Customer name
            </label>
            <input
              id="customer-name"
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="e.g. Ajeet Kumar"
              className="w-full rounded-xl surface-pressed px-4 py-3 font-body text-sm text-ink placeholder:text-ink-soft/50 outline-none"
            />
          </div>

          <div>
            <label htmlFor="customer-phone" className="block text-sm font-semibold text-ink mb-1.5">
              Phone number
            </label>
            <input
              id="customer-phone"
              type="tel"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              placeholder="e.g. 9564839272"
              className="w-full rounded-xl surface-pressed px-4 py-3 font-body text-sm text-ink placeholder:text-ink-soft/50 outline-none"
            />
          </div>

          {formError && <p className="text-sm text-debit font-medium">{formError}</p>}

          <div className="flex items-center gap-3 pt-2">
            <Button type="submit" variant="brass" disabled={isSubmitting} className="flex-1">
              {isSubmitting ? 'Creating…' : 'Create customer'}
            </Button>
            <Button type="button" variant="ghost" onClick={() => navigate(-1)} disabled={isSubmitting}>
              Cancel
            </Button>
          </div>
        </form>
      </div>
    </AppShell>
  );
}
