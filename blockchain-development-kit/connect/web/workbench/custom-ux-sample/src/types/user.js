import { defaultUserColor } from '../common/constants';
import { randomColor, hashAlphaNum } from '../common/utils';

export default class User {
  constructor(id, externalId, firstName, lastName, emailAddress) {
    this.id = id || 0;
    this.externalId = externalId || '';
    this.firstName = firstName || '';
    this.lastName = lastName || '';
    this.emailAddress = emailAddress || '';
    this.color = this.externalId ?
      randomColor(hashAlphaNum(this.externalId)) : defaultUserColor;
  }

  get initials() {
    const [firstChar, secondChar] = this.firstName.split(' ').map(str => str.charAt(0).toUpperCase());
    return firstChar + (this.lastName.charAt(0).toUpperCase() || secondChar || '');
  }

  get displayName() {
    if (this.lastName) {
      return `${this.firstName} ${this.lastName}`;
    }

    return this.firstName;
  }

  get displayColor() {
    return this.color;
  }

  getShortName(length = 20) {
    if (this.displayName.length < length) {
      return this.displayName;
    }

    return this.displayName.slice(0, length).concat('...');
  }

  static parseWBUser(wbUserObject) {
    return new User(
      wbUserObject.userID,
      wbUserObject.externalID,
      wbUserObject.firstName,
      wbUserObject.lastName,
      wbUserObject.emailAddress,
    );
  }

  static parseAdalUser(adalObject) {
    const { profile } = adalObject;
    const firstName = profile.given_name || profile.name;
    const lastName = profile.family_name || '';
    const emailAddress = profile.email || profile.unique_name || adalObject.userName;

    return new User(null, profile.oid, firstName, lastName, emailAddress);
  }
}
